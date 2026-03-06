package com.ruoyi.system.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.exception.base.BaseException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.*;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.service.IOrderStatusTimelineService;
import com.ruoyi.system.service.IWalletBalanceService;
import com.ruoyi.system.util.DefaultValueUtil;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IOrderService;
import com.ruoyi.system.strategy.OrderCreate.OrderCreateContext;
import com.ruoyi.system.util.LogEsUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 订单表(Order)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:02
 */
@Service("orderService")
@Slf4j
public class OrderServiceImpl implements IOrderService {


    @Resource
    private CommodityMapper commodityMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Resource
    private OrderCommodityMapper orderCommodityMapper;

    @Resource
    private PromoCodeRecordsMapper promoCodeRecordsMapper;

    @Resource
    private PromoRecordsMapper  promoRecordsMapper;

    @Resource
    private OrderCommodityResourcesMapper orderCommodityResourcesMapper;

    @Resource
    private ServerResourcesMapper serverResourcesMapper;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private IWalletBalanceService iWalletBalanceService;



    @Resource
    private OrderRenewalResourcesMapper orderRenewalResourcesMapper;

    @Resource
    private IOrderStatusTimelineService orderStatusTimelineService;

    @Resource
    private OrderInformationSnapshotMapper orderInformationSnapshotMapper;


    /**
     * [直接从商品创建订单]
     *
     * @param orderByCommodityDto 创建参数
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/10
     **/
    @Override
    @Transactional
    public Result createOrderByCommodity(OrderByCommodityDto orderByCommodityDto) {
        //默认赋值
        setDefaultValue(orderByCommodityDto);
        //查询推广码
        PromoCodeRecords promoCodeRecords = promoCodeRecordsMapper.selectPromoCode(orderByCommodityDto.getPromoCode());
        if (StrUtil.isNotBlank(orderByCommodityDto.getPromoCode())&&ObjectUtil.isNull(promoCodeRecords)){
            LogEsUtil.warn("推广码不存在或已过期，推广码："+orderByCommodityDto.getPromoCode());
            return Result.fail("邀请码已过期");
        }
        //校验商品是否存在
        //一锁
        Commodity normalCommodity = commodityMapper.selectByIdForUpdate(orderByCommodityDto.getCommodityId());
        if (normalCommodity == null) {
            LogEsUtil.warn("商品不存在或已下架，商品id：" + orderByCommodityDto.getCommodityId());
            return Result.fail("商品不存在");
        }
        //商品库存变更
        updateCommodityStock(orderByCommodityDto, normalCommodity);
        //创建订单
        OrderCreateContext orderCreateContext = new OrderCreateContext(orderByCommodityDto.getPayCycle());
        Order order = orderCreateContext.createOrder(orderByCommodityDto, normalCommodity, promoCodeRecords);
        //进行订单商品数据创建，30分钟取消订单准备
        int insert = orderMapper.insert(order);
        if (insert<1){
            LogEsUtil.warn("生成订单失败");
            throw new RuntimeException("生成订单失败，请稍后再试");
        }
        LogEsUtil.warn("生成订单信息：" + order);
        //时间线设置
        orderStatusTimelineService.createOrderAndTimeline(order.getId());
        //创建订单商品记录
        insertOrderCommodity( order, orderByCommodityDto);
        //创建推广记录
        insertPromoRecords(promoCodeRecords,order);
        //创建购买商品时候的商品快照
        insertOrderInformationSnapshot(orderByCommodityDto.getCommodityId(),order.getId(),null);
        return Result.success(order);
    }


    //插入订单快照信息
    private void insertOrderInformationSnapshot(String commodityId,String orderId,String ip){
        OrderInformationSnapshot orderInformationSnapshot = commodityMapper.selectSnapshotByCommodityId(commodityId);
        if(orderInformationSnapshot == null){
            LogEsUtil.warn("查询订单资源失败");
            throw new RuntimeException("生成订单资源快照失败");
        }
        orderInformationSnapshot.setOrderId(orderId);
        orderInformationSnapshot.setUserId(SecurityUtils.getStrUserId());
        if (StrUtil.isNotBlank( ip)){
            orderInformationSnapshot.setIp(ip);
        }
        int insert = orderInformationSnapshotMapper.insert(orderInformationSnapshot);
        if (insert<1){
            LogEsUtil.info("生成订单快照资源失败");
            throw new RuntimeException("生成订单资源快照失败");
        }
        LogEsUtil.info("生成订单快照资源成功：" + orderInformationSnapshot);
    }


    /**
     * [资源续费订单创建]
     *
     * @param orderByRenewalDto 创建参数
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/10/28
     **/
    @Override
    @Transactional
    public Result createOrderByRenewal(OrderByRenewalDto orderByRenewalDto) {
        //查询推广码
        PromoCodeRecords promoCodeRecords = promoCodeRecordsMapper.selectPromoCode(orderByRenewalDto.getPromoCode());
        if (StrUtil.isNotBlank(orderByRenewalDto.getPromoCode())&&ObjectUtil.isNull(promoCodeRecords)){
            LogEsUtil.warn("推广码不存在或已过期，推广码："+orderByRenewalDto.getPromoCode());
            return Result.fail("邀请码已过期");
        }
        //一锁
        ServerResources resourcesMapperById = serverResourcesMapper.findByIdForUpdate(orderByRenewalDto.getResourcesId());
        //二判
        if (resourcesMapperById == null){
            LogEsUtil.warn("资源不存在或已下架，资源id：" + orderByRenewalDto.getResourcesId());
            return Result.fail("资源不存在");
        }
        if (AvailableStatus.AVAILABLE_STATUS_DOWN.equals(resourcesMapperById.getAvailableStatus())){
            LogEsUtil.warn("资源已下架，资源id：" + orderByRenewalDto.getResourcesId());
            return Result.fail("资源已下架");
        }
        if (!SecurityUtils.getStrUserId().equals(resourcesMapperById.getResourceTenant())){
            LogEsUtil.warn("资源不属于当前用户，资源id：" + orderByRenewalDto.getResourcesId());
            return Result.fail("资源不属于当前用户");
        }
        if (resourcesMapperById.getLeaseExpirationTime().before(new Date())){
            LogEsUtil.warn("资源已到期，资源id：" + orderByRenewalDto.getResourcesId());
            return Result.fail("资源已到期,无法进行续费");
        }
        //这里商品下架也是可以续费的
        Commodity byResourcesId = commodityMapper.findByResourcesId(resourcesMapperById.getId());
        if (byResourcesId == null){
            LogEsUtil.warn("续费资源所属商品不存在，资源id：" + orderByRenewalDto.getResourcesId());
            return Result.fail("续费资源所属商品不存在");
        }
        //创建订单计算上下文
        OrderCreateContext orderCreateContext = new OrderCreateContext(orderByRenewalDto.getPayCycle());
        Order renewalOrder = orderCreateContext.createRenewalOrder(byResourcesId, promoCodeRecords);
        int insert = orderMapper.insert(renewalOrder);
        if (insert<1){
            LogEsUtil.warn("生成订单失败");
            throw new RuntimeException("生成订单失败，请稍后再试");
        }
        LogEsUtil.info("生成订单信息：" + renewalOrder);
        orderStatusTimelineService.createOrderAndTimeline(renewalOrder.getId());
        //新增推广记录
        insertPromoRecords(promoCodeRecords,renewalOrder);
        //新增续费资源记录
        insertOrderRenewal(renewalOrder, resourcesMapperById);
        //生成订单快照
        insertOrderInformationSnapshot(byResourcesId.getId(),renewalOrder.getId(),resourcesMapperById.getResourcesIp());
        return Result.success(renewalOrder);
    }


    private static void setDefaultValue(OrderByCommodityDto orderByCommodityDto) {
        try {
            DefaultValueUtil.setDefaultData(orderByCommodityDto);
        } catch (IllegalAccessException e) {
            log.error("默认赋值失败"+e.getMessage());
            throw new RuntimeException("默认赋值失败");
        }
    }


    private OrderCommodity insertOrderCommodity( Order order, OrderByCommodityDto orderByCommodityDto) {
        OrderCommodity orderCommodity = new OrderCommodity();
        orderCommodity.setOrderId(order.getId());
        orderCommodity.setCommodityId(orderByCommodityDto.getCommodityId());
        orderCommodity.setUserId(order.getUserId());
        orderCommodity.setOrderQuantity(0);
        orderCommodity.setPurchaseQuantity(orderByCommodityDto.getNum());
        orderCommodity.setCreateUser(order.getUserId());
        orderCommodity.setUpdateUser(order.getUserId());
        int insert = orderCommodityMapper.insert(orderCommodity);
        if (insert<1){
            LogEsUtil.warn("订单商品信息生成失败");
            throw new RuntimeException("生成订单失败，请稍后再试");
        }
        LogEsUtil.info("订单商品信息：" + orderCommodity);
        return orderCommodity;
    }

    private void insertOrderRenewal(Order order, ServerResources serverResources) {
        OrderRenewalResources orderRenewalResources = OrderRenewalResources.builder()
                .orderId(order.getId())
                .resourcesId(serverResources.getId())
                .resourcesIp(serverResources.getResourcesIp())
                .userId(order.getUserId())
                .build();
        int insert = orderRenewalResourcesMapper.insert(orderRenewalResources);
        LogEsUtil.info("订单续费资源信息：" + orderRenewalResources);
    }


    private PromoRecords insertPromoRecords(PromoCodeRecords promoCodeRecords,Order order) {
        if (promoCodeRecords== null){
            return null;
        }
        PromoRecords promoRecords = new PromoRecords();
        promoRecords.setUserId(promoCodeRecords.getUserId());
        promoRecords.setReferralsUserId(SecurityUtils.getStrUserId());
        promoRecords.setPromoCodeRecordsId(promoCodeRecords.getId());
        promoRecords.setOrderId(order.getId());
        BigDecimal amount = order.getAmount();
        promoRecords.setCashbackAmount(amount.multiply(new BigDecimal("0.1")));
        promoRecords.setPurchaseAmount(amount);
        promoRecords.setStatus(OrderStatus.WAIT_CONFIRM);
        promoRecords.setCashbackPercentage("10%");
        int insert = promoRecordsMapper.insert(promoRecords);
        if (insert<1){
            LogEsUtil.warn("订单推广信息生成失败：" + promoRecords);
            throw new RuntimeException("生成订单失败，请稍后再试");
        }
        LogEsUtil.info("订单推广信息：" + promoRecords);
        return promoRecords;
    }



    private void updateCommodityStock(OrderByCommodityDto orderByCommodityDto,  Commodity normalCommodity){
            Boolean commodityStockStatus = reduceCommodityStock(orderByCommodityDto, normalCommodity);
            if (!commodityStockStatus){
                LogEsUtil.warn("商品库存不足,商品id：" + normalCommodity.getId());
                throw new RuntimeException("商品库存不足");
            }
            commodityMapper.update(normalCommodity);

    }


    private static OrderMessageDto buildOrderMessageDto(OrderByCommodityDto orderByCommodityDto, Order order, PromoCodeRecords promoCodeRecords) {
        //创建消息队列类
        OrderMessageDto orderMessageDto = new OrderMessageDto();
        orderMessageDto.setOrder(order);
        orderMessageDto.setOrderByCommodityDto(orderByCommodityDto);
        orderMessageDto.setPromoCodeRecordsDto(promoCodeRecords);
        return orderMessageDto;
    }


    //减少商品库存
    private Boolean reduceCommodityStock(OrderByCommodityDto orderByCommodityDto, Commodity normalCommodity) {
        //判断库存是否满足，满足则直接扣减库存并返回
        if (normalCommodity.getInventory()>= orderByCommodityDto.getNum()){
            int i = normalCommodity.getInventory() - orderByCommodityDto.getNum();
            normalCommodity.setInventory(i);
            LogEsUtil.info("商品id为[" + normalCommodity.getId()+"],扣减的库存为["+orderByCommodityDto.getNum()+"]");
            return true;
        }else {
            //判断库存加未超卖数量是否满足购买数量
            if (normalCommodity.getInventory()+(normalCommodity.getOversoldConfigurations() - normalCommodity.getOversold()) >= orderByCommodityDto.getNum()){
                //判断还需要扣减的超卖数
                int needOversold = orderByCommodityDto.getNum() - normalCommodity.getInventory();
                int primitiveInventory = normalCommodity.getInventory();
                //将库存扣减为0，超卖数加上需要扣减的超卖数
                normalCommodity.setInventory(0);
                normalCommodity.setOversold(normalCommodity.getOversold()+needOversold);
                LogEsUtil.info("商品id为[" + normalCommodity.getId()+"],扣减的库存为["+primitiveInventory+"],增加的超卖数为["+needOversold+"]");
                return true;
            }else {
                return false;
            }
        }
    }


    /**
     * [分页查询订单]
     *
     * @param orderDto 分页查询订单
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/14
     **/
    @Override
    public Result pageQuery(OrderDto orderDto) {
        PageHelper.startPage(orderDto);
        List<OrderVo> orderVoIPage = orderMapper.queryPage(orderDto, SecurityUtils.getStrUserId());
        return Result.success(new PageInfo<>(orderVoIPage));
    }


    /**
     * [订单取消新版（不使用消息队列）]
     *
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/10/17
     **/
    @Override
    @Transactional
    public Result cancelOrderNew(String orderId) {
        //一锁
        Order order = orderMapper.queryByIdForUpdate(orderId);
        //二判
        if (order == null){
            LogEsUtil.warn("订单不存在：" + orderId);
            return Result.fail("订单不存在");
        }
        if (!StrUtil.equals(OrderTypeConstant.ADD,order.getOrderType())){
            LogEsUtil.warn("订单类型不为新购，订单id为：" + orderId);
            return Result.fail("该订单不是新购订单");
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
        boolean timeBeforeHours = isTimeBeforeHours(byOrderId.getCompletedTime(), 24);
        if (!timeBeforeHours){
            LogEsUtil.info("订单已超过规定的可退款期限,订单id为：" + orderId);
            return Result.fail("当前订单已超过规定的可退款期限");
        }
        //释放资源
        releaseResources(orderId);
        OrderByCommodityDto commodityByOrderId = orderCommodityMapper.findCommodityByOrderId(orderId);
        //增加库存
        addCommodityStockLock(orderId,commodityByOrderId.getCommodityId(), commodityByOrderId.getNum());
        //撤销推广
        cancelPromo(order);
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
     * [分页查询订单-客服]
     *
     * @param orderDto 查询参数
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/2/26
     **/
    @Override
    public Result pageQueryService(OrderDto orderDto) {
        PageHelper.startPage(orderDto);
        List<OrderVo> orderVoIPage = orderMapper.queryPage(orderDto, orderDto.getUserId());
        return Result.success(new PageInfo<>(orderVoIPage));
    }

    /**
     * [续费订单取消]
     *
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/1/19
     **/
    @Override
    public Result cancelOrderRenewal(String orderId) {
        //一锁
        Order order = orderMapper.queryByIdForUpdate(orderId);
        //二判
        if (order == null){
            LogEsUtil.warn("订单不存在：" + orderId);
            return Result.fail("订单不存在");
        }
        if (!StrUtil.equals(OrderTypeConstant.RENEW,order.getOrderType())){
            LogEsUtil.warn("订单类型不为续费，订单id为：" + orderId);
            return Result.fail("该订单不是续费订单");
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
            LogEsUtil.info("订单正在退款中,订单id为：" + orderId);
            return Result.fail("订单正在退款中");
        }
        //已退款
        if (OrderStatus.REFUND_SUCCESS.equals(order.getStatus())){
            LogEsUtil.info("订单已退款,订单id为：" + orderId);
            return Result.fail("订单已退款");
        }
        LogEsUtil.info("订单取消,订单id为："+orderId);
        OrderStatusTimelineVo byOrderId = orderStatusTimelineService.getByOrderId(orderId);
        boolean timeBeforeHours = isTimeBeforeHours(byOrderId.getCompletedTime(), 24);
        if (!timeBeforeHours){
            LogEsUtil.info("订单已超过规定的可退款期限,订单id为：" + orderId);
            return Result.fail("当前订单已超过规定的可退款期限");
        }
        OrderRenewalResourcesVo orderRenewalResources = orderRenewalResourcesMapper.findByOrderId(orderId);

        //释放资源,并判断是否需要释放库存
        boolean needAddCommodityStockLock = releaseResourcesRenewal(orderId, order,orderRenewalResources);
        if (needAddCommodityStockLock){
            //增加库存
            addCommodityStockLock(orderId,orderRenewalResources.getCommodityId(), 1);
        }
        //撤销推广
        cancelPromo(order);
        //订单时间线
        orderStatusTimelineService.setUserCanceledAndWaitRefundTime(orderId);
        String status = null;
        String result = null;
        if (OrderStatus.WAIT_PAY.equals(order.getStatus())){
            status = OrderStatus.USER_CANCELED;
            result = "订单取消成功";
            LogEsUtil.info("订单取消成功,订单id为："+orderId);
        }else {
            status = OrderStatus.WAIT_REFUND;
            result = "订单退款成功";
            LogEsUtil.info("订单退款成功,订单id为："+orderId);
        }
        orderMapper.updateStatusById(orderId, status);
        return Result.success(result);
    }


    /**
     * 传入一个时间，判断这个时间是否在多少小时之前,
     * 例如当时时间2026/1/18 14：40：00  传入时间 2026/1/17 13：40：00 hours 为24
     * 传入时间距今已超过24小时，返回false
     * @param targetDate 目标时间
     * @param hours 小时数
     * @return true: 目标时间在距当前hours小时之前
     */
    public static boolean isTimeBeforeHours(Date targetDate, int hours) {
        if (targetDate == null) {
            //订单未推进到已完成状态或极限情况，不存在时间线，直接退款
            return true;
        }

        Date now = new Date();
        long thresholdMillis = now.getTime() - (hours * 60 * 60 * 1000L);
        Date thresholdDate = new Date(thresholdMillis);

        LogEsUtil.info("订单可退款时间为:"+thresholdDate+" 之前,订单完结时间为:"+targetDate);
        return targetDate.getTime() > thresholdMillis;
    }



    /**
     * [根据订单id获取支付二维码]
     *
     * @param orderId  订单id
     * @param payaType 支付方式，可选支付宝或微信
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/10/24
     **/
    @Override
    public Result getQrCode(String orderId, String payaType) {
        //获取订单状态
        Order order = orderMapper.queryById(orderId);
        Result<?> validStatus = validStatus(orderId, order);
        if (validStatus != null){
            return validStatus;
        }
        YiPayResponse yiPayResponse = getOrderInfo(order);
        if (ObjectUtil.isNotNull(yiPayResponse)){
            LogEsUtil.info("订单已扫码支付，订单id："+orderId);
            return Result.success("订单已支付",true);
        }else {
            OrderPayUrlVo orderPayUrlVo = new OrderPayUrlVo();
            orderPayUrlVo.setPayUrl(getPayUrl(orderId, payaType));
            orderPayUrlVo.setOrderId(orderId);
            orderPayUrlVo.setPayType(payaType);
            orderPayUrlVo.setPayMoney(order.getAmount());
            return Result.success(orderPayUrlVo);
        }
    }


    private String getPayUrl(String orderId, String payaType){
        if (OrderStatus.ALIPAY_PAY.equals(payaType)){

        } else if (OrderStatus.WECHAT_PAY.equals(payaType)) {

        }else {
            throw new RuntimeException("支付方式错误");
        }
        RequestThreePayVo forObject = restTemplate.getForObject("https://mock.apipost.net/mock/335560/getCode?apipost_id=391baed8798152", RequestThreePayVo.class);
        if (ObjectUtil.isNull(forObject)){
            throw new RuntimeException("获取支付二维码失败");
        }
        return forObject.getUrl();
    }


    private void releaseResources(String orderId){
        List<OrderCommodityResources> byOrderId = orderCommodityResourcesMapper.findByOrderId(orderId);
        //查询这个订单下面资源，如果没有不释放
        List<String> collect = byOrderId.stream().map(OrderCommodityResources::getResourcesId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty( collect)){
            return;
        }
        int i = serverResourcesMapper.updateServerResourcesSaleStatus(collect);
        LogEsUtil.info("释放新购资源成功,订单id为"+orderId+",资源id为："+collect.get(0));
    }

    //续费订单释放资源,为true需要释放库存，false不需要
    private boolean releaseResourcesRenewal(String orderId,Order order, OrderRenewalResourcesVo byOrderId){
        //若订单未完成返回false，代表不需要回复库存
        if (!StrUtil.equals(order.getStatus(),OrderStatus.COMPLETED)){
            log.info("订单未完成,订单id为："+orderId);
            return false;
        }
        if (byOrderId == null){
            log.info("订单续费资源不存在,订单id为："+orderId);
            return false;
        }
        ServerResources byIdForUpdate = serverResourcesMapper.findByIdForUpdate(byOrderId.getResourcesId());
        if(byIdForUpdate == null){
            log.info("资源不存在,订单id为："+orderId+",资源id为："+byOrderId.getResourcesId());
            return false;
        }
        //获取订单的续费周期
        String paymentPeriod = order.getPaymentPeriod();
        //计算租赁时间减去周期后的时间，即未续费时间
        Date date = calculateRenewalDate(byIdForUpdate.getLeaseExpirationTime(), paymentPeriod);
        LogEsUtil.info("订单续费资源剔除续费周期后的时间为："+date);
        //若时间小于当前时间，则返回true，需要释放库存
        if (ObjectUtil.isNull(date)||date.before(new Date())){
            int i = serverResourcesMapper.updateServerResourcesSaleStatus(Arrays.asList(byIdForUpdate.getId()));
            LogEsUtil.info("释放续费资源成功,当前订单已过期，需要释放库存,订单id为:"+orderId+",资源id为："+byIdForUpdate.getId());
            return true;
        }else {
            //将过期时间置为date
            byIdForUpdate.setLeaseExpirationTime( date);
            serverResourcesMapper.updateById(byIdForUpdate);
            LogEsUtil.info("订单续费资源未过期,已扣除续费周期,订单id为:"+orderId+",资源id为："+byIdForUpdate.getId());
            return false;
        }

    }


    /**
     * 计算续费前一个周期的时间
     * @param date 原始时间
     * @param period 续费周期
     * @return 减去一个周期后的时间
     */
    private static Date calculateRenewalDate(Date date, String period) {
        if (date == null) {
            LogEsUtil.info("原始时间为空");
            throw new IllegalArgumentException("时间不能为空");
        }

        if (period == null) {
            LogEsUtil.info("续费周期为空");
            throw new IllegalArgumentException("续费周期不能为空");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        switch (period) {
            case PaymentPeriodConstant.MONTHLY:
                // 减去一个月
                calendar.add(Calendar.DAY_OF_YEAR, -30);
                break;
            case PaymentPeriodConstant.QUARTERLY:
                // 减去三个月
                calendar.add(Calendar.DAY_OF_YEAR, -90);
                break;
            case PaymentPeriodConstant.YEARLY:
                // 减去一年
                calendar.add(Calendar.DAY_OF_YEAR, -365);
                break;
            default:
                return new Date();
        }

        return calendar.getTime();
    }


    //撤销推广
    private void cancelPromo(Order order) {
        //查询订单
        PromoRecords promoRecords = promoRecordsMapper.findByOrderId(order.getId());
        if (promoRecords != null){
            //如果订单未支付
            if (OrderStatus.WAIT_PAY.equals(order.getStatus())){
                promoRecords.setStatus(OrderStatus.CANCELED);
            }else {
                promoRecords.setStatus(OrderStatus.REFUND);
            }
            LogEsUtil.info("订单推广撤销成功,订单id为"+order.getId()+",推广记录id为："
                    +promoRecords.getId()+",订单状态为："+order.getStatus()+",推广记录状态为："+promoRecords.getStatus());
            int i = promoRecordsMapper.updateById(promoRecords);
            if (i <= 0){
                LogEsUtil.warn("撤销订单推广失败"+promoRecords.getId());
                throw new RuntimeException("撤销订单推广失败");
            }
        }

    }


    //撤销时回滚商品库存
    private void addCommodityStockLock(String orderId,String commodityId,int num) {
        //一锁
        Commodity normalCommodity = commodityMapper.selectByIdForUpdate(commodityId);
        //二判
        if ( normalCommodity == null){
            LogEsUtil.warn("商品不存在,订单id为"+orderId+",商品id为："+commodityId);
            //没有查询到商品直接结束，不阻塞主流程
            return;
        }
        addCommodityStock(num, normalCommodity);
        commodityMapper.update(normalCommodity);
        LogEsUtil.info("商品库存变更成功,订单id为:"+orderId+",商品id为：" + normalCommodity.getId());
    }


    private void addCommodityStock(int buyNUm, Commodity normalCommodity) {
        if (normalCommodity.getOversold()==0){
            normalCommodity.setInventory(normalCommodity.getInventory()+buyNUm);
        }else {
            normalCommodity.setOversold(normalCommodity.getOversold()-buyNUm);
        }
    }


    /**
     * [计算价格]
     *
     * @param orderByCommodityDto
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/24
     **/
    @Override
    public Result calculatePrice(OrderByCommodityDto orderByCommodityDto) {
        //校验商品是否存在
        Commodity normalCommodity = commodityMapper.findNormalCommodity(orderByCommodityDto.getCommodityId());
        if (normalCommodity == null) {
            return Result.fail("商品不存在");
        }
        setDefaultValue(orderByCommodityDto);

        //查询推广码
        PromoCodeRecords promoCodeRecords;
        if (StrUtil.isNotEmpty(orderByCommodityDto.getPromoCode())) {
            promoCodeRecords = promoCodeRecordsMapper.selectPromoCode(orderByCommodityDto.getPromoCode());
            if (promoCodeRecords == null){
                return Result.fail("推广码不存在，请重试");
            }
        } else {
            promoCodeRecords = null;
        }
        PriceCalculateVo priceCalculateVo = new PriceCalculateVo();
        //按月价格计算
        CompletableFuture<Void> monthlyCalculatePrice = CompletableFuture.runAsync(() -> {
            //创建订单
            OrderCreateContext orderCreateContext = new OrderCreateContext(PaymentPeriodConstant.MONTHLY);
            BigDecimal price = orderCreateContext.calculatePrice(orderByCommodityDto, normalCommodity, promoCodeRecords);
            priceCalculateVo.setMonthlyPrice(price.setScale(2, RoundingMode.HALF_UP).toString());
            if (ObjectUtil.isNotEmpty(promoCodeRecords)) {
                priceCalculateVo.setMonthlyDiscountPrice("10%");
            }
        });

        //按季付款
        CompletableFuture<Void> quarterlyCalculatePrice = CompletableFuture.runAsync(() -> {
            //创建订单
            OrderCreateContext orderCreateContext = new OrderCreateContext(PaymentPeriodConstant.QUARTERLY);
            BigDecimal price = orderCreateContext.calculatePrice(orderByCommodityDto, normalCommodity, promoCodeRecords);
            priceCalculateVo.setQuarterlyPrice(price.setScale(2, RoundingMode.HALF_UP).toString());
            if (ObjectUtil.isNotEmpty(promoCodeRecords)) {
                priceCalculateVo.setQuarterlyDiscountPrice("14.5%");
            } else {
                priceCalculateVo.setQuarterlyDiscountPrice("5.0%");
            }
        });

        //按年付款
        CompletableFuture<Void> yearCalculatePrice = CompletableFuture.runAsync(() -> {
            //创建订单
            OrderCreateContext orderCreateContext = new OrderCreateContext(PaymentPeriodConstant.YEARLY);
            //价格保留两位小数
            BigDecimal price = orderCreateContext.calculatePrice(orderByCommodityDto, normalCommodity, promoCodeRecords);
            priceCalculateVo.setYearlyPrice(price.setScale(2, RoundingMode.HALF_UP).toString());
            if (ObjectUtil.isNotEmpty(promoCodeRecords)) {
                priceCalculateVo.setYearlyDiscountPrice("19.0%");
            } else {
                priceCalculateVo.setYearlyDiscountPrice("10.0%");
            }
        });
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(yearCalculatePrice, quarterlyCalculatePrice, monthlyCalculatePrice);
        try {
            voidCompletableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("计算价格失败" + e.getMessage());
            throw new RuntimeException("等待计算价格失败，请稍后再试");
        }
        //计算价格
        return Result.success(priceCalculateVo);
    }




    /**
     * [获取订单信息]
     *
     * @param orderId
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/24
     **/
    @Override
    public Result getOrderInfo(String orderId) {
        OrderInfoVo order = orderMapper.getOrderInfoById(orderId,SecurityUtils.getStrUserId());
        if (order == null){
            return Result.fail("订单不存在");
        }
        if (!SecurityUtils.getStrUserId().equals(order.getUserId())){
            return Result.fail("您没有权限查看此订单");
        }
        PromoCodeRecords byOrderId = promoCodeRecordsMapper.findByOrderId(orderId);
        order.setPromoCode(byOrderId == null ? "" : byOrderId.getPromoCode());
        if (PaymentPeriodConstant.YEARLY.equals(order.getPayCycle())){
            if (ObjectUtil.isNotEmpty(byOrderId)) {
                order.setDiscountPrice("19.0%");
            } else {
                order.setDiscountPrice("10.0%");
            }
        } else if (PaymentPeriodConstant.QUARTERLY.equals(order.getPayCycle())) {
            if (ObjectUtil.isNotEmpty(byOrderId)) {
                order.setDiscountPrice("14.5%");
            } else {
                order.setDiscountPrice("5.0%");
            }
        }else {
            if (ObjectUtil.isNotEmpty(byOrderId)) {
                order.setDiscountPrice("10.0%");
            }
        }
        return Result.success(order);
    }


    /**
     * [获取续费订单信息]
     *
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/11/14
     **/
    @Override
    public Result getRenewalOrderInfo(String orderId) {
        OrderInfoVo order = orderMapper.getRenewalOrderInfoById(orderId,SecurityUtils.getStrUserId());
        if (order == null){
            return Result.fail("订单不存在");
        }
        if (!SecurityUtils.getStrUserId().equals(order.getUserId())){
            return Result.fail("您没有权限查看此订单");
        }
        PromoCodeRecords byOrderId = promoCodeRecordsMapper.findByOrderId(orderId);
        order.setPromoCode(byOrderId == null ? "" : byOrderId.getPromoCode());
        if (PaymentPeriodConstant.YEARLY.equals(order.getPayCycle())){
            if (ObjectUtil.isNotEmpty(byOrderId)) {
                order.setDiscountPrice("19.0%");
            } else {
                order.setDiscountPrice("10.0%");
            }
        } else if (PaymentPeriodConstant.QUARTERLY.equals(order.getPayCycle())) {
            if (ObjectUtil.isNotEmpty(byOrderId)) {
                order.setDiscountPrice("14.5%");
            } else {
                order.setDiscountPrice("5.0%");
            }
        }else {
            if (ObjectUtil.isNotEmpty(byOrderId)) {
                order.setDiscountPrice("10.0%");
            }
        }
        return Result.success(order);
    }


    /**
     * [查询订单详情]
     *
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/11/19
     **/
    @Override
    public Result getOrderDetailById(String orderId) {
        OrderDetailVo order = orderMapper.getOrderDetailById(orderId);
        if(ObjectUtil.isNull( order)){
            return Result.fail("订单不存在");
        }
        if (!SecurityUtils.hasPermi()&&!SecurityUtils.getStrUserId().equals(order.getUserId())){
            return Result.fail("您没有权限查看此订单");
        }
        OrderStatusTimelineVo byOrderId = orderStatusTimelineService.getByOrderId(orderId);
        order.setOrderStatusTimelineVo(byOrderId);
        return Result.success(order);
    }


    /**
     * [获取订单状态]
     *
     * @param orderId
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/10/16
     **/
    @Override
    public Result getOrderStatus(String orderId) {
        //获取订单状态
        Order order = orderMapper.queryById(orderId);
        //判断订单是否存在
        if (order == null||order.getIsDeleted()==0){
            return Result.fail("订单不存在");
        }
        String strUserId = SecurityUtils.getStrUserId();
        if (!strUserId.equals(order.getCreateUser())){
            return Result.fail("您没有权限对此订单进行操作");
        }
        if (!OrderStatus.WAIT_PAY.equals(order.getStatus())){
            return Result.success(orderStatusConvert(order.getStatus()),false);
        }
        YiPayResponse yiPayResponse = getOrderInfo(order);
        if (ObjectUtil.isNotNull(yiPayResponse)){
            LogEsUtil.info("订单已扫码支付，订单id："+orderId);
            return Result.success("订单已支付",false);
        }
        return Result.success(true);
    }

    //获取订单支付状态,已支付为true、未支付为false
    private Boolean getOrderPayStatus(String orderId) {
        return Math.random()>0.8;
    }

    //调用订单平台获取订单信息
    private YiPayResponse getOrderInfo(Order order){
        boolean b = Math.random() > 0.3;
        return b?new YiPayResponse():null;
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
     * [用户点击订单已支付]
     *
     * @param orderId
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/10/16
     **/
    @Override
    @Transactional
    public Result orderIsPay(String orderId,Boolean isBalance) {
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

    /**
     * [续费订单支付]
     *
     * @param orderId   订单id
     * @param isBalance 是否使用余额
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/10/29
     **/
    @Override
    @Transactional
    public Result renewalOrderIsPay(String orderId, Boolean isBalance) {
        //获取订单状态
        //一锁【订单】
        Order order = orderMapper.queryByIdForUpdate(orderId);
        Result<?> validStatus = validStatus(orderId, order);
        if (validStatus != null){
            return validStatus;
        }
        OrderServiceImpl bean = SpringUtil.getBean(OrderServiceImpl.class);
        //如果第三方已支付，则走第三方支付,未支付则判断是否余额支付，是走余额支付，否则支付失败
        YiPayResponse yiPayResponse = getOrderInfo(order);
        if (ObjectUtil.isNotNull(yiPayResponse)){
            order.setPaymentType(yiPayResponse.getPayType());
            return bean.renewalResources(orderId, order);
        }else {
            return bean.renewalNoThreePay(orderId, isBalance, order, bean);
        }
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



    public Result<Boolean> renewalNoThreePay(String orderId, Boolean isBalance, Order order, OrderServiceImpl bean) {
        if (isBalance){
            order.setPaymentType(OrderStatus.BALANCE_PAY);
            Boolean reduceBalance = iWalletBalanceService.reduceBalance(order);
            if (!reduceBalance){
                LogEsUtil.warn("用户余额不足");
                return Result.success("余额不足，请充值", false);
            }
            return bean.renewalResources(orderId, order);
        }else {
            LogEsUtil.warn("未检测到支付信息");
            return Result.success("未检测到支付信息，请稍后再试", false);
        }
    }



    //分配资源
    public Result<Boolean> allocationResources(String orderId, Order order) {
        //分配资源，新增订单资源
        OrderCommodity orderCommodity = orderCommodityMapper.findByOrderId(order.getId());
        orderStatusTimelineService.setUserPayAndWaitAllocationTime(orderId);
        ServerResources resources = allocationResources(order,orderCommodity);
        //如果资源id为空，则订单置为待分配资源状态
        if (ObjectUtil.isNull(resources)){
            order.setStatus(OrderStatus.WAIT_ALLOCATION_RESOURCES);
            orderMapper.updateById(order);
        }else {
            String resourcesId = resources.getId();
            OrderCommodityResources orderCommodityResources = buildOrderCommodityResources(order, orderCommodity.getId(), resourcesId);
            orderCommodityResourcesMapper.insert(orderCommodityResources);
            order.setStatus(OrderStatus.COMPLETED);
            orderMapper.updateById(order);
            orderStatusTimelineService.setCompletedTime(orderId);
            orderInformationSnapshotMapper.updateIpByOrderId(orderId,resources.getResourcesIp());
            LogEsUtil.info("订单分配资源成功,订单id："+orderId+",资源id："+resourcesId);
        }
        return Result.success("支付成功",true);
    }


    //续费订单处理资源
    public Result<Boolean> renewalResources(String orderId, Order order) {
        //已经支付后锁住该订单，做相关操作
        //查询订单
        //一锁
        ServerResources serverResources = serverResourcesMapper.findByOrderIdForUpdate(order.getId());
        orderStatusTimelineService.setUserPayAndWaitAllocationTime(orderId);
        renewalResources(order,serverResources);
        //将订单置为已完成状态
        order.setStatus(OrderStatus.COMPLETED);
        orderMapper.updateById(order);
        //订单时间线
        orderStatusTimelineService.setCompletedTime(orderId);
        return Result.success("支付成功",true);
    }



    private OrderCommodityResources buildOrderCommodityResources(Order order, String orderCommodityId, String resourcesId) {
        OrderCommodityResources orderCommodityResources = new OrderCommodityResources();
        orderCommodityResources.setUserId(order.getUserId());
        orderCommodityResources.setOrderCommodityId(orderCommodityId);
        orderCommodityResources.setResourcesId(resourcesId);
        return orderCommodityResources;
    }

    //分配资源
    private ServerResources allocationResources(Order order,OrderCommodity orderCommodity) {
        if (orderCommodity == null){
            LogEsUtil.warn("未查询到订单购买商品,订单id:"+order.getId());
            throw new BaseException("未查询到订单购买商品，请重新购买或提交工单");
        }
        ServerResources byCommodityId = serverResourcesMapper.findByCommodityId(orderCommodity.getCommodityId());
        if (ObjectUtil.isNull(byCommodityId)){
            return null;
        }
        Date timeByPaymentPeriod = getTimeByPaymentPeriod(order.getPaymentPeriod(),new Date());
        //更新到期时间
        byCommodityId.setLeaseExpirationTime(timeByPaymentPeriod);
        byCommodityId.setResourceTenant(order.getUserId());
        byCommodityId.setSalesStatus(SalesStatus.ON_SALE);
        int i = serverResourcesMapper.updateById(byCommodityId);
        if (i < 1){
            LogEsUtil.warn("资源分配失败,订单id:"+order.getId()+",资源id："+byCommodityId.getId());
            return null;
        }else {
            return byCommodityId;
        }
    }

    //续费资源
    private String renewalResources(Order order,ServerResources byCommodityId) {
         if(ObjectUtil.isNull(byCommodityId)){
             LogEsUtil.warn("未查询到续费资源,订单id:"+order.getId());
            return null;
        }
         //判断是否到期,到期则在当前时间上续费，未到期则在到期时间上续费
         Date nowDate = null;
         if (ObjectUtil.isNull(byCommodityId.getLeaseExpirationTime())|| byCommodityId.getLeaseExpirationTime().getTime() < System.currentTimeMillis()){
             nowDate = new Date();
         }else {
             nowDate = byCommodityId.getLeaseExpirationTime();
         }
        Date timeByPaymentPeriod = getTimeByPaymentPeriod(order.getPaymentPeriod(),nowDate);
        //更新到期时间
        byCommodityId.setLeaseExpirationTime(timeByPaymentPeriod);
        byCommodityId.setResourceTenant(order.getUserId());
        byCommodityId.setSalesStatus(SalesStatus.ON_SALE);
        int i = serverResourcesMapper.updateById(byCommodityId);
        if (i < 1){
            LogEsUtil.warn("资源续费失败,订单id:"+order.getId()+",资源id："+byCommodityId.getId());
            throw new BaseException("资源续费失败，请创建工单联系管理员");
        }else {
            return byCommodityId.getId();
        }
    }


    //通过付款周期获取到期时间
    private Date getTimeByPaymentPeriod(String paymentPeriod,Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        switch (paymentPeriod) {
            case PaymentPeriodConstant.MONTHLY:
                calendar.add(Calendar.DAY_OF_YEAR, 30);
                break;
            case PaymentPeriodConstant.QUARTERLY:
                calendar.add(Calendar.DAY_OF_YEAR, 90);
                break;
            case PaymentPeriodConstant.YEARLY:
                calendar.add(Calendar.DAY_OF_YEAR, 365);
                break;
        }
        return calendar.getTime();
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


}
