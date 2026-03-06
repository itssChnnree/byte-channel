package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.OrderStatus;
import com.ruoyi.system.constant.OrderTypeConstant;
import com.ruoyi.system.constant.PaymentPeriodConstant;
import com.ruoyi.system.constant.TicketStatus;
import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.dto.OrderQuoteDto;
import com.ruoyi.system.domain.dto.TicketMainTextQuoteDto;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.domain.vo.OrderStatusTimelineVo;
import com.ruoyi.system.domain.vo.OrderVo;
import com.ruoyi.system.domain.vo.YiPayResponse;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.mapstruct.OrderMapstruct;
import com.ruoyi.system.service.IOrderQuoteService;
import com.ruoyi.system.domain.vo.OrderQuoteVo;
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
    private IWalletBalanceService iWalletBalanceService;

    @Resource
    OrderMapstruct orderMapstruct;

    @Resource
    private RestTemplate restTemplate;

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
    public Result cancelQuoteOrder(String orderId) {
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
        String strUserId = SecurityUtils.getStrUserId();
        if (!strUserId.equals(order.getCreateUser())){
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
            result = "订单取消成功";
        }else {
            status = OrderStatus.WAIT_REFUND;
            result = "订单退款成功";
        }
        orderMapper.updateStatusById(orderId, status);
        LogEsUtil.info("订单取消成功,订单id为：{}",orderId);
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
        Result<?> validStatus = validStatus(orderId, order);
        if (validStatus != null){
            return validStatus;
        }
        //如果第三方已支付，则走第三方支付,未支付则判断是否余额支付，是走余额支付，否则支付失败
        YiPayResponse yiPayResponse = getOrderInfo(order);
        if (ObjectUtil.isNotNull(yiPayResponse)){
            order.setPaymentType(yiPayResponse.getPayType());
            LogEsUtil.info("订单已使用聚合支付，支付id["+orderId+"],支付方式为["+order.getPaymentType()+"]");
            return allocationResources(orderId, order);
        }else {
            return noThreePay(orderId, isBalance, order);
        }
    }


    //分配资源
    public Result<Boolean> allocationResources(String orderId, Order order) {
        //分配资源，新增订单资源
        orderStatusTimelineService.setUserPayAndWaitAllocationTime(orderId);
        order.setStatus(OrderStatus.WAIT_ALLOCATION_RESOURCES);
        orderMapper.updateById(order);
        //更新报价单状态
        TicketMainTextQuote ticketMainTextQuote
                = ticketMainTextQuoteMapper.findByOrderId(orderId);
        ticketMainTextQuote.setStatus(TicketStatus.WAITING_RESOURCE_ALLOCATION);
        ticketMainTextQuoteMapper.updateById(ticketMainTextQuote);
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

    //调用订单平台获取订单信息
    private YiPayResponse getOrderInfo(Order order){
        boolean b = Math.random() > 0.3;
        return b?new YiPayResponse():null;
    }

    private Result<?> validStatus(String orderId, Order order) {
        //判断订单是否存在
        if (order == null|| order.getIsDeleted()!=0){
            LogEsUtil.info("订单不存在："+orderId);
            return Result.fail("订单不存在");
        }
        String strUserId = SecurityUtils.getStrUserId();
        if (!strUserId.equals(order.getCreateUser())){
            LogEsUtil.info("用户无权访问此订单："+orderId);
            return Result.fail("您没有权限对此订单进行操作");
        }
        //如果订单不是待支付状态，代表不用确认付款
        if (!OrderStatus.WAIT_PAY.equals(order.getStatus())){
            LogEsUtil.info("订单状态不是待支付："+orderId);
            return Result.success(orderStatusConvert(order.getStatus()), false);
        }
        return null;
    }

    //订单状态转化
    private String orderStatusConvert(String status) {
        switch (status) {
            case "WAIT_PAY":
                return "待支付";
            case "WAIT_ALLOCATION_RESOURCES":
                return "订单已支付，待分配资源";
            case "ALLOCATION_RESOURCES":
                return "资源分配中";
            case "COMPLETED":
                return "已完成";
            case "USER_CANCELED":
                return "用户主动取消";
            case "CANCELED_TIMEOUT":
                return "订单超时自动取消";
            case "WAIT_REFUND":
                return "订单待退款中";
            case "REFUND_SUCCESS":
                return "订单已退款";
            default:
                return "订单状态异常";
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
}
