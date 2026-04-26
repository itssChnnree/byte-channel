package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.*;
import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.dto.OrderQuoteDto;
import com.ruoyi.system.domain.dto.ProcessQuoteDto;
import com.ruoyi.system.domain.dto.TicketMainTextQuoteDto;
import com.ruoyi.system.domain.dto.UpdateQuoteRecordDto;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.mapstruct.OrderMapstruct;
import com.ruoyi.system.service.IOrderBaseService;
import com.ruoyi.system.service.IOrderQuoteService;
import com.ruoyi.system.service.IOrderStatusTimelineService;
import com.ruoyi.system.service.IWalletBalanceService;
import com.ruoyi.system.util.LogEsUtil;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 订单报价表(OrderQuote)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-02-27 00:11:14
 */
@Service("orderQuoteService")
public class OrderQuoteServiceImpl implements IOrderQuoteService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderQuoteMapper orderQuoteMapper;

    @Resource
    private IOrderBaseService orderBaseService;

    @Resource
    private IWalletBalanceService iWalletBalanceService;

    @Resource
    OrderMapstruct orderMapstruct;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private RedisCache redisCache;

    @Resource
    private IOrderStatusTimelineService orderStatusTimelineService;


    @Resource
    private TicketMainTextQuoteMapper ticketMainTextQuoteMapper;

    @Resource
    private TicketMapper ticketMapper;

    @Resource
    private TicketMainTextMapper ticketMainTextMapper;



    /**
     * [取消报价订单]
     *
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/3/1
     **/
    @Override
    @Transactional
    public Result cancelQuoteOrder(String orderId,Boolean refoundToBalance) {
        //一锁
        Order order = orderMapper.queryByIdForUpdate(orderId);
        //二判
        if (order == null){
            LogEsUtil.warn("订单不存在：" + orderId);
            return Result.fail("订单不存在");
        }
        if (!StrUtil.equals(OrderTypeConstant.QUOTE,order.getOrderType())){
            LogEsUtil.warn("订单类型不为报价，订单id为：" + orderId);
            return Result.fail("该订单不是报价订单");
        }

        if (!SecurityUtils.hasPre(order.getCreateUser())){
            LogEsUtil.warn("用户对订单没有操作权限，订单id为：" + orderId);
            return Result.fail("对该订单暂无操作权限");
        }
        //已取消 超时系统取消
        if ( OrderStatus.CANCELED_TIMEOUT.equals(order.getStatus())){
            LogEsUtil.warn("订单超时自动取消，订单id为：" + orderId);
            return Result.fail("订单超时自动取消");
        }
        //已取消 用户主动取消
        if (OrderStatus.USER_CANCELED.equals(order.getStatus())){
            LogEsUtil.warn("用户已取消订单，订单id为：" + orderId);
            return Result.fail("用户已取消订单");
        }
        //待退款
        if (OrderStatus.WAIT_REFUND.equals(order.getStatus())){
            LogEsUtil.warn("订单正在退款中，订单id为：" + orderId);
            return Result.fail("订单正在退款中");
        }
        //已退款
        if (OrderStatus.REFUND_SUCCESS.equals(order.getStatus())){
            LogEsUtil.warn("订单已退款，订单id为：" + orderId);
            return Result.fail("订单已退款");
        }
        LogEsUtil.info("订单开始取消,订单id为：" + orderId);
        OrderStatusTimelineVo byOrderId = orderStatusTimelineService.getByOrderId(orderId);
        if(!ObjectUtils.isEmpty(byOrderId) && !ObjectUtils.isEmpty(byOrderId.getCompletedTime())){
            LogEsUtil.warn("订单已完成，订单id为：" + orderId);
            return Result.fail("报价订单已完成，无法退款");
        }
        //订单时间线
        orderStatusTimelineService.setUserCanceledAndWaitRefundTime(orderId);
        String status = null;
        String result = null;
        if (OrderStatus.WAIT_PAY.equals(order.getStatus())){
            status = OrderStatus.USER_CANCELED;
            result = ResultMessage.CANCEL_ORDER_SUCCESS;
        }else {
            status = OrderStatus.WAIT_REFUND;
            result = ResultMessage.REFUND_ORDER_SUCCESS;
        }
        orderMapper.refoundById(orderId, status, orderBaseService.refoundToBalance(refoundToBalance));
        LogEsUtil.info("订单取消成功,订单id为：{}",orderId);
        updateTicketMainTextQuote(orderId,TicketStatus.QUOTE_USER_CANCEL);
        return Result.success(result);
    }



    /**
     * [报价订单支付]
     *
     * @param orderId   订单id
     * @param isBalance 是否余额支付
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/3/1
     **/
    @Override
    @Transactional
    public Result quoteOrderIsPay(String orderId, Boolean isBalance) {
        //获取订单状态
        //一锁
        Order order = orderMapper.queryByIdForUpdate(orderId);
        Result<?> validStatus = orderBaseService.validStatus(orderId, order);
        if (validStatus != null){
            return validStatus;
        }
        //如果第三方已支付，则走第三方支付,未支付则判断是否余额支付，是走余额支付，否则支付失败
        YiPayResponse yiPayResponse = orderBaseService.getOrderInfo(order);
        if (ObjectUtil.isNotNull(yiPayResponse)){
            order.setPaymentType(yiPayResponse.getPayType());
            order.setPaymentId(yiPayResponse.getPayId());
            orderBaseService.addProfit(order,"报价商品");
            LogEsUtil.info("订单已使用聚合支付，支付id["+orderId+"],支付方式为["+order.getPaymentType()+"]");
            return allocationResources(orderId, order);
        }else {
            return noThreePay(orderId, isBalance, order);
        }
    }


    //分配资源
    public Result<Boolean> allocationResources(String orderId, Order order) {
        //分配资源，新增订单资源
        //修改订单时间线
        orderStatusTimelineService.setUserPayAndWaitAllocationTime(orderId);
        //变更订单状态
        order.setStatus(OrderStatus.WAIT_ALLOCATION_RESOURCES);
        orderMapper.updateById(order);
        //更新报价单状态
        updateTicketMainTextQuote(orderId, TicketStatus.WAITING_RESOURCE_ALLOCATION);
        TicketMainTextQuote ticketMainTextQuote = ticketMainTextQuoteMapper.findByOrderId(orderId);
        //更新工单状态
        TicketMainText ticketMainText = ticketMainTextMapper.selectById(ticketMainTextQuote.getTicketMainTextId());
        ticketMapper.updateStatusById(ticketMainText.getTicketId(),TicketStatus.WAITING_SERVICE_REPLY);
        LogEsUtil.info("报价单支付成功,ticketMainTextQuote:"+ticketMainTextQuote+",order:"+order);
        return Result.success("支付成功",true);
    }


    public Result<Boolean> noThreePay(String orderId, Boolean isBalance, Order order) {
        if (isBalance){
            order.setPaymentType(OrderStatus.BALANCE_PAY);
            Boolean reduceBalance = iWalletBalanceService.reduceBalance(order);
            if (!reduceBalance){
                return Result.success("余额不足，请充值", false);
            }
            LogEsUtil.info("订单已使用余额支付，支付id["+orderId+"]");
            return allocationResources(orderId, order);
        }else {
            return Result.success("未检测到支付信息，请稍后再试", false);
        }
    }

    /**
     * [查询订单详情页-报价处理记录信息]
     *
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/3/1
     **/
    @Override
    public Result getQuoteOrderInfo(String orderId) {
        Order order = orderMapper.queryById(orderId);
        if (order == null){
            return Result.fail("订单不存在");
        }
        if (!SecurityUtils.getStrUserId().equals(order.getUserId())){
            return Result.fail("您没有权限查看此订单");
        }
        if (!OrderTypeConstant.QUOTE.equals(order.getOrderType())){
            return Result.fail("订单类型不为报价");
        }
        OrderVo orderVo = orderMapstruct.change2Vo(order);
        return Result.success(orderVo);
    }

    /**
     * 创建报价订单
     *
     * @param ticketMainTextQuoteDto
     * @return
     */
    @Override
    @Transactional
    public Result createOrderQuote(TicketMainTextQuoteDto ticketMainTextQuoteDto) {
        //查询报价单
        TicketMainTextQuote ticketMainTextQuote
                = ticketMainTextQuoteMapper.selectById(ticketMainTextQuoteDto.getId());
        if (ObjectUtils.isEmpty(ticketMainTextQuote)){
            LogEsUtil.warn("用户处理了不存在的报价单："+ticketMainTextQuote);
            return Result.fail("该报价不存在");
        }
        if(!TicketStatus.QUOTE_NOT_PROCESSED.equals(ticketMainTextQuote.getStatus())){
            LogEsUtil.warn("用户处理了非待处理的报价单："+ticketMainTextQuote);
            return Result.fail("订单状态非法");
        }

        Ticket ticket = ticketMapper.selectByQuoteId(ticketMainTextQuote.getId());
        if (ticket==null || !StrUtil.equals(ticket.getUserId(), SecurityUtils.getStrUserId())){
            LogEsUtil.warn("用户处理了非自己创建的工单："+ticket+",ticketMainTextQuote:"+ticketMainTextQuote);
            return Result.fail("您没有权限处理此报价");
        }
        TicketMainText ticketMainText = ticketMainTextMapper.selectById(ticketMainTextQuote.getTicketMainTextId());
        if (ObjectUtils.isEmpty(ticketMainText)){
            LogEsUtil.warn("用户处理了不存在的工单正文："+ticketMainText);
            return Result.fail("该报价不存在");
        }
        if (ticketMainTextQuoteDto.getIsAccept()){
            Order order = buildOrder(ticketMainTextQuote,ticketMainText);
            int insert = orderMapper.insert(order);
            if (insert<=0){
                LogEsUtil.warn("用户创建订单失败："+order+",ticketMainTextQuoteDto:"+ticketMainTextQuoteDto);
                return Result.fail("创建订单失败");
            }
            orderStatusTimelineService.createOrderAndTimeline(order.getId());
            LogEsUtil.info("用户创建订单成功："+order+",ticketMainTextQuoteDto:"+ticketMainTextQuoteDto);
            //更新报价单状态
            ticketMainTextQuote.setStatus(TicketStatus.QUOTE_WAITING_PAYMENT);
            ticketMainTextQuote.setQuoteOrderId(order.getId());
            ticketMainTextQuoteMapper.updateById(ticketMainTextQuote);
            return Result.success("已接受报价");
        }else {
            //更新报价单状态
            ticketMainTextQuote.setStatus(TicketStatus.QUOTE_REJECTED);
            ticketMainTextQuoteMapper.updateById(ticketMainTextQuote);
            return Result.success("已拒绝报价");
        }
    }

    private Order buildOrder(TicketMainTextQuote ticketMainTextQuote,TicketMainText ticketMainText){
        Order order = new Order();
        order.setUserId(SecurityUtils.getStrUserId());
        order.setAmount(ticketMainTextQuote.getQuote());
        order.setDescription(ticketMainText.getTicketMainText());
        order.setOrderType(OrderTypeConstant.QUOTE);
        order.setPaymentPeriod(PaymentPeriodConstant.SINGLE);
        order.setStatus(OrderStatus.WAIT_PAY);
        order.setOrderTime(new Date());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setIsDeleted(0);
        return order;
    }

    /**
     * [处理报价订单]
     *
     * @param processQuoteDto 处理参数
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/4/6
     **/
    @Override
    @Transactional
    public Result processQuoteOrder(ProcessQuoteDto processQuoteDto) {
        // 1. 查询订单
        Order order = orderMapper.queryByIdForUpdate(processQuoteDto.getOrderId());
        if (order == null) {
            LogEsUtil.warn("订单不存在：" + processQuoteDto.getOrderId());
            return Result.fail("订单不存在");
        }

        // 2. 校验订单类型
        if (!StrUtil.equals(OrderTypeConstant.QUOTE, order.getOrderType())) {
            LogEsUtil.warn("订单类型不为报价，订单id为：" + processQuoteDto.getOrderId());
            return Result.fail("该订单不是报价订单");
        }

        // 3. 查询或创建 OrderQuote 记录
        OrderQuote orderQuote = orderQuoteMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderQuote>()
                .eq(OrderQuote::getOrderId, order.getId())
                .eq(OrderQuote::getIsDeleted, 0)
        );

        if (orderQuote == null) {
            // 创建新记录
            orderQuote = new OrderQuote();
            orderQuote.setId(cn.hutool.core.util.IdUtil.simpleUUID());
            orderQuote.setOrderId(order.getId());
            orderQuote.setUserId(order.getUserId());
            orderQuote.setQuoteProcessingRecord(processQuoteDto.getQuoteProcessingRecord());
            orderQuote.setCreateTime(new Date());
            orderQuote.setUpdateTime(new Date());
            orderQuote.setIsDeleted(0);
            orderQuoteMapper.insert(orderQuote);
            LogEsUtil.info("创建报价处理记录成功，订单ID：{}", order.getId());
        } else {
            // 更新记录
            orderQuote.setQuoteProcessingRecord(processQuoteDto.getQuoteProcessingRecord());
            orderQuote.setUpdateTime(new Date());
            orderQuoteMapper.updateById(orderQuote);
            LogEsUtil.info("更新报价处理记录成功，订单ID：{}", order.getId());
        }

        // 4. 变更订单状态为 COMPLETED
        orderMapper.updateStatusById(order.getId(), OrderStatus.COMPLETED);
        
        // 5. 更新订单状态时间线
        orderStatusTimelineService.setCompletedTime(order.getId());

        LogEsUtil.info("报价订单处理成功，订单ID：{}", order.getId());
        return Result.success("报价处理成功");
    }

    /**
     * [修改报价处理记录]
     *
     * @param updateQuoteRecordDto 修改参数
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/4/6
     **/
    @Override
    @Transactional
    public Result updateQuoteRecord(UpdateQuoteRecordDto updateQuoteRecordDto) {
        // 1. 查询订单
        Order order = orderMapper.queryById(updateQuoteRecordDto.getOrderId());
        if (order == null) {
            LogEsUtil.warn("订单不存在：" + updateQuoteRecordDto.getOrderId());
            return Result.fail("订单不存在");
        }

        // 2. 校验订单类型
        if (!StrUtil.equals(OrderTypeConstant.QUOTE, order.getOrderType())) {
            LogEsUtil.warn("订单类型不为报价，订单id为：" + updateQuoteRecordDto.getOrderId());
            return Result.fail("该订单不是报价订单");
        }

        // 3. 查询 OrderQuote 记录
        OrderQuote orderQuote = orderQuoteMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderQuote>()
                .eq(OrderQuote::getOrderId, order.getId())
                .eq(OrderQuote::getIsDeleted, 0)
        );

        if (orderQuote == null) {
            LogEsUtil.warn("报价处理记录不存在，订单ID：{}", order.getId());
            return Result.fail("报价处理记录不存在");
        }

        // 4. 更新记录（不改变订单状态）
        orderQuote.setQuoteProcessingRecord(updateQuoteRecordDto.getQuoteProcessingRecord());
        orderQuote.setUpdateTime(new Date());
        orderQuoteMapper.updateById(orderQuote);

        LogEsUtil.info("修改报价处理记录成功，订单ID：{}", order.getId());
        return Result.success("修改成功");
    }

    /**
     * [订单信息页-报价处理记录查询]
     *
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/4/15
     **/
    @Override
    public Result getQuoteOrderRecord(String orderId) {
        OrderQuoteVo orderQuoteVo = orderQuoteMapper.selectOrderQuoteVoByOrderId(orderId);
        if (ObjectUtils.isEmpty(orderQuoteVo)){
            return Result.success();
        }
        if(!SecurityUtils.hasPre(orderQuoteVo.getUserId())){
            return Result.fail("您没有权限查看此订单");
        }
        return Result.success(orderQuoteVo);
    }

    /**
     * [根据订单id修改报价单状态]
     *
     * @param orderId 订单id
     * @param status  状态
     * @return void
     * @author 陈湘岳 2026/4/18
     **/
    @Override
    public void updateTicketMainTextQuote(String orderId, String status) {
        //查询工单正文报价单并修改状态
        TicketMainTextQuote ticketMainTextQuote = ticketMainTextQuoteMapper.findByOrderIdForUpdate(orderId);
        ticketMainTextQuote.setStatus(status);
        ticketMainTextQuoteMapper.updateById(ticketMainTextQuote);
    }
}
