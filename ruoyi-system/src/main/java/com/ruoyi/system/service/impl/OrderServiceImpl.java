package com.ruoyi.system.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.exception.base.BaseException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.*;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.util.DefaultValueUtil;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IOrderService;
import com.ruoyi.system.strategy.OrderCreate.OrderCreateContext;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
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
    private WalletBalanceMapper walletBalanceMapper;

    @Resource
    private WalletBalanceDetailMapper walletBalanceDetailMapper;


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
        //校验商品是否存在
        Commodity normalCommodity = commodityMapper.findNormalCommodity(orderByCommodityDto.getCommodityId());
        if (normalCommodity == null) {
            return Result.fail("商品不存在");
        }
        //默认赋值
        setDefaultValue(orderByCommodityDto);
        //商品库存变更
        updateCommodityStock(orderByCommodityDto, normalCommodity);
        //创建订单
        OrderCreateContext orderCreateContext = new OrderCreateContext(orderByCommodityDto.getPayCycle());
        //查询推广码
        PromoCodeRecords promoCodeRecords = promoCodeRecordsMapper.selectPromoCode(orderByCommodityDto.getPromoCode());
        Order order = orderCreateContext.createOrder(orderByCommodityDto, normalCommodity, promoCodeRecords);
        //订单发送消息队列，进行订单商品数据创建，30分钟取消订单准备
        int insert = orderMapper.insert(order);
        if (insert<1){
            throw new RuntimeException("生成订单失败，请稍后再试");
        }
        insertOrderCommodity( order, orderByCommodityDto);
        insertPromoRecords(promoCodeRecords,order);
        //发送消息，若
        return Result.success(order);
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
        ServerResources byId = serverResourcesMapper.findById(orderByRenewalDto.getResourcesId());
        if (byId == null){
            return Result.fail("资源不存在");
        }
        if (AvailableStatus.AVAILABLE_STATUS_DOWN.equals(byId.getAvailableStatus())){
            return Result.fail("资源已下架");
        }
        if (!SecurityUtils.getStrUserId().equals(byId.getResourceTenant())||byId.getLeaseExpirationTime().before(new Date())){
            return Result.fail("资源不属于当前用户或资源已到期");
        }
        Commodity byResourcesId = commodityMapper.findByResourcesId(byId.getId());
        if (byResourcesId == null){
            return Result.fail("续费资源所属商品不存在");
        }
        //创建订单计算上下文
        OrderCreateContext orderCreateContext = new OrderCreateContext(orderByRenewalDto.getPayCycle());
        PromoCodeRecords promoCodeRecords = promoCodeRecordsMapper.selectPromoCode(orderByRenewalDto.getPromoCode());
        Order renewalOrder = orderCreateContext.createRenewalOrder(byResourcesId, promoCodeRecords);
        int insert = orderMapper.insert(renewalOrder);
        if (insert<1){
            throw new RuntimeException("生成订单失败，请稍后再试");
        }
        //新增推广记录
        insertPromoRecords(promoCodeRecords,renewalOrder);

        insertRenewalOrderCommodity(renewalOrder, byResourcesId, byId.getId());
        return Result.success(renewalOrder);
    }

    private void updateCommodityStock(OrderByCommodityDto orderByCommodityDto, Commodity normalCommodity) {
        RLock lock = redissonClient.getLock(RedissonLockStatus.COMMODITY_STOCK_CHANGE_LOCK + orderByCommodityDto.getCommodityId());
        try{
            //变更商品库存
            updateCommodityStock(orderByCommodityDto, lock, normalCommodity);
        }catch (InterruptedException e){
            log.error("获取锁异常", e);
            throw new RuntimeException("系统繁忙，请稍后再试");
        }finally {
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
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
            throw new RuntimeException("生成订单失败，请稍后再试");
        }
        return orderCommodity;
    }

    private void insertRenewalOrderCommodity( Order order,Commodity commodity,String resourcesId) {
        OrderCommodity orderCommodity = new OrderCommodity();
        orderCommodity.setOrderId(order.getId());
        orderCommodity.setCommodityId(commodity.getId());
        orderCommodity.setUserId(order.getUserId());
        orderCommodity.setOrderQuantity(0);
        orderCommodity.setPurchaseQuantity(1);
        orderCommodity.setCreateUser(order.getUserId());
        orderCommodity.setUpdateUser(order.getUserId());
        int insert = orderCommodityMapper.insert(orderCommodity);
        if (insert<1){
            throw new RuntimeException("生成订单失败，请稍后再试");
        }
        OrderCommodityResources orderCommodityResources = new OrderCommodityResources();
        orderCommodityResources.setUserId(SecurityUtils.getStrUserId());
        orderCommodityResources.setOrderCommodityId(orderCommodity.getId());
        orderCommodityResources.setResourcesId(resourcesId);
        orderCommodityResources.setIsDeleted(0);
        int insert1 = orderCommodityResourcesMapper.insert(orderCommodityResources);
        if (insert1<1){
            throw new RuntimeException("生成订单失败，请稍后再试");
        }
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
            throw new RuntimeException("生成订单失败，请稍后再试");
        }
        return promoRecords;
    }



    private void updateCommodityStock(OrderByCommodityDto orderByCommodityDto, RLock lock, Commodity normalCommodity) throws InterruptedException {
        boolean lockStatus = lock.tryLock(5, TimeUnit.SECONDS);
        if (lockStatus){
            Boolean commodityStockStatus = reduceCommodityStock(orderByCommodityDto, normalCommodity);
            if (!commodityStockStatus){
                throw new RuntimeException("商品库存不足");
            }
            commodityMapper.update(normalCommodity);
        }else {
            throw new RuntimeException("系统繁忙，请稍后再试");
        }
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
            normalCommodity.setInventory(normalCommodity.getInventory()-orderByCommodityDto.getNum());
            return true;
        }else {
            //判断库存加未超卖数量是否满足购买数量
            if (normalCommodity.getInventory()+(normalCommodity.getOversoldConfigurations()- normalCommodity.getOversold())>= orderByCommodityDto.getNum()){
                //判断还需要扣减的超卖数
                int needOversold = orderByCommodityDto.getNum() - normalCommodity.getInventory();
                //将库存扣减为0，超卖数加上需要扣减的超卖数
                normalCommodity.setInventory(0);
                normalCommodity.setOversold(normalCommodity.getOversold()+needOversold);
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
    public Result cancelOrderNew(String orderId) {
        Order order = orderMapper.queryById(orderId);
        if (order == null){
            return Result.fail("订单不存在");
        }
        if ( OrderStatus.CANCELED_TIMEOUT.equals(order.getStatus())){
            return Result.fail("订单超时自动取消");
        }
        if (OrderStatus.USER_CANCELED.equals(order.getStatus())){
            return Result.fail("用户已取消订单");
        }
        if (OrderStatus.WAIT_REFUND.equals(order.getStatus())){
            return Result.fail("订单正在退款中");
        }
        if (OrderStatus.REFUND_SUCCESS.equals(order.getStatus())){
            return Result.fail("订单已退款");
        }
        String strUserId = SecurityUtils.getStrUserId();
        if (!strUserId.equals(order.getCreateUser())){
            return Result.fail("对该订单暂无操作权限");
        }
        CompletableFuture<OrderByCommodityDto> uCompletableFuture = CompletableFuture.supplyAsync(() -> orderCommodityMapper.findCommodityByOrderId(orderId));
        CompletableFuture<Commodity> completableFuture = CompletableFuture.supplyAsync(() -> commodityMapper.findCommodityByOrderId(orderId));
        //撤销推广
        cancelPromo(order);
        //释放资源
        releaseResources(orderId);
        //增加库存
        addCommodityStockLock(uCompletableFuture, completableFuture);
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
        return Result.success(result);
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
        if (getOrderPayStatus(orderId)){
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



    //退款
    private void refund(Order order) {

    }



    private void releaseResources(String orderId){
        List<OrderCommodityResources> byOrderId = orderCommodityResourcesMapper.findByOrderId(orderId);
        List<String> collect = byOrderId.stream().map(OrderCommodityResources::getResourcesId).collect(Collectors.toList());
        int i = serverResourcesMapper.updateServerResourcesSaleStatus(collect);
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
            int i = promoRecordsMapper.updateById(promoRecords);
            if (i <= 0){
                log.error("撤销订单推广失败"+promoRecords.getId());
                throw new RuntimeException("撤销订单推广失败");
            }
        }

    }


    //撤销时回滚商品库存
    private void addCommodityStockLock(CompletableFuture<OrderByCommodityDto> orderByCommodityDtoCompletableFuture,
                                       CompletableFuture<Commodity> normalCommodityFuture) {
        OrderByCommodityDto orderByCommodityDto = null;
        Commodity normalCommodity = null;
        try {
            orderByCommodityDto = orderByCommodityDtoCompletableFuture.get(5, TimeUnit.SECONDS);
            normalCommodity = normalCommodityFuture.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("获取库存及商品失败",e);
            throw new RuntimeException("系统繁忙，请稍后再试");
        }
        if (orderByCommodityDto == null && normalCommodity == null){
            throw new RuntimeException("订单库存或商品不存在");
        }
        RLock lock = redissonClient.getLock(RedissonLockStatus.COMMODITY_STOCK_CHANGE_LOCK + normalCommodity.getId());
        try{
            boolean lockStatus = lock.tryLock(5, TimeUnit.SECONDS);
            if (lockStatus){
                addCommodityStock(orderByCommodityDto, normalCommodity);
                commodityMapper.update(normalCommodity);
            }else {
                throw new RuntimeException("系统繁忙，请稍后再试");
            }
        }catch (InterruptedException e){
            log.error("获取锁异常", e);
            throw new RuntimeException("系统繁忙，请稍后再试");
        }finally {
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }


    private void addCommodityStock(OrderByCommodityDto orderByCommodity, Commodity normalCommodity) {
        if (normalCommodity.getOversold()==0){
            normalCommodity.setInventory(normalCommodity.getInventory()+orderByCommodity.getNum());
        }else {
            normalCommodity.setOversold(normalCommodity.getOversold()-orderByCommodity.getNum());
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
        OrderInfoVo order = orderMapper.getOrderInfoById(orderId,SecurityUtils.getStrUserId());
        if (order == null){
            return Result.fail("订单不存在");
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
        if (getOrderPayStatus(orderId)){
            return Result.success("订单已支付",false);
        }
        return Result.success(true);
    }

    //获取订单支付状态,已支付为true、未支付为false
    private Boolean getOrderPayStatus(String orderId) {
        return Math.random()>0.5;
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
    public Result orderIsPay(String orderId,Boolean isBalance) {
        //获取订单状态
        Order order = orderMapper.queryById(orderId);
        Result<?> validStatus = validStatus(orderId, order);
        if (validStatus != null){
            return validStatus;
        }
        OrderServiceImpl bean = SpringUtil.getBean(OrderServiceImpl.class);
        //如果第三方已支付，则走第三方支付,未支付则判断是否余额支付，是走余额支付，否则支付失败
        if (getOrderPayStatus(orderId)){
            return bean.allocationResources(orderId, order);
        }else {
            return bean.noThreePay(orderId, isBalance, order, bean);
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
    public Result renewalOrderIsPay(String orderId, Boolean isBalance) {
        //获取订单状态
        Order order = orderMapper.queryById(orderId);
        Result<?> validStatus = validStatus(orderId, order);
        if (validStatus != null){
            return validStatus;
        }
        OrderServiceImpl bean = SpringUtil.getBean(OrderServiceImpl.class);
        //如果第三方已支付，则走第三方支付,未支付则判断是否余额支付，是走余额支付，否则支付失败
        if (getOrderPayStatus(orderId)){
            return bean.renewalResources(orderId, order);
        }else {
            return bean.renewalNoThreePay(orderId, isBalance, order, bean);
        }
    }

    @Transactional
    public Result<Boolean> noThreePay(String orderId, Boolean isBalance, Order order, OrderServiceImpl bean) {
        if (isBalance){
            Boolean reduceBalance = reduceBalance(order);
            if (!reduceBalance){
                return Result.success("余额不足，请充值", false);
            }
            order.setPaymentType(OrderStatus.BALANCE_PAY);
            return bean.allocationResources(orderId, order);
        }else {
            return Result.success("未检测到支付信息，请稍后再试", false);
        }
    }


    @Transactional
    public Result<Boolean> renewalNoThreePay(String orderId, Boolean isBalance, Order order, OrderServiceImpl bean) {
        if (isBalance){
            order.setPaymentType(OrderStatus.BALANCE_PAY);
            Boolean reduceBalance = reduceBalance(order);
            if (!reduceBalance){
                return Result.success("余额不足，请充值", false);
            }
            return bean.renewalResources(orderId, order);
        }else {
            return Result.success("未检测到支付信息，请稍后再试", false);
        }
    }


    //余额扣减
    private Boolean reduceBalance(Order order){
        String userId = SecurityUtils.getStrUserId();
        RLock lock = redissonClient.getLock(RedissonLockStatus.USER_BALANCE_LOCK + userId);
        try{
            if (lock.tryLock()) {
                //获取用户余额
                WalletBalance walletBalanceByUserId = walletBalanceMapper.findWalletBalanceByUserId(userId);
                //判断余额是否充足
                if (ObjectUtil.isNull(walletBalanceByUserId)) {
                    throw new RuntimeException("查询用户余额错误，请联系管理员");
                }
                if (walletBalanceByUserId.getBalance().compareTo(order.getAmount()) < 0){
                    return false;
                }
                //余额充足，则扣减余额
                walletBalanceByUserId.setBalance(walletBalanceByUserId.getBalance().subtract(order.getAmount()));
                int i = walletBalanceMapper.updateById(walletBalanceByUserId);
                if (i >= 1){
                    //添加余额变更记录
                    walletBalanceDetailMapper.insert(buildWalletBalanceDetail(walletBalanceByUserId, order));
                    return true;
                }else {
                    throw new RuntimeException("余额变更失败，请联系管理员");
                }
            }else {
                throw new RuntimeException("余额变更中，请稍后再试");
            }
        }finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


    private WalletBalanceDetail buildWalletBalanceDetail(WalletBalance walletBalance, Order order) {
        WalletBalanceDetail walletBalanceDetail = new WalletBalanceDetail();
        walletBalanceDetail.setUserId(walletBalance.getUserId());
        walletBalanceDetail.setChangeAmount(order.getAmount().doubleValue());
        walletBalanceDetail.setType(BalanceDetailStatus.REDUCE);
        return walletBalanceDetail;
    }


    //分配资源
    @Transactional
    public Result<Boolean> allocationResources(String orderId, Order order) {
        //已经支付后锁住该订单，做相关操作
        RLock lock = redissonClient.getLock(RedissonLockStatus.ORDER_STATUS_UPDATE_LOCK + orderId);
        try{
            if (lock.tryLock()) {
                //分配资源，新增订单资源
                OrderCommodity orderCommodity = orderCommodityMapper.findByOrderId(order.getId());
                String resourcesId = allocationResources(order,orderCommodity);
                //如果订单id为空，则订单置为待分配资源状态
                if (StrUtil.isBlank(resourcesId)){
                    order.setStatus(OrderStatus.WAIT_ALLOCATION_RESOURCES);
                    orderMapper.updateById(order);
                }else {
                    OrderCommodityResources orderCommodityResources = buildOrderCommodityResources(order, orderCommodity.getId(), resourcesId);
                    orderCommodityResourcesMapper.insert(orderCommodityResources);
                    order.setStatus(OrderStatus.COMPLETED);
                    orderMapper.updateById(order);
                    orderCommodity.setOrderQuantity(orderCommodity.getOrderQuantity() + 1);
                    orderCommodityMapper.updateById(orderCommodity);
                }
            }else {
                return Result.success("订单正在处理中", false);
            }
        }finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return Result.success("支付成功",true);
    }


    //续费订单处理资源
    @Transactional
    public Result<Boolean> renewalResources(String orderId, Order order) {
        //已经支付后锁住该订单，做相关操作
        RLock lock = redissonClient.getLock(RedissonLockStatus.ORDER_STATUS_UPDATE_LOCK + orderId);
        try{
            if (lock.tryLock()) {
                //分配资源，新增订单资源
                ServerResources serverResources = serverResourcesMapper.findByOrderId(order.getId());
                String resourcesId = renewalResources(order,serverResources);
                //如果订单id为空，则订单置为待分配资源状态
                order.setStatus(OrderStatus.COMPLETED);
                orderMapper.updateById(order);
            }else {
                return Result.success("订单正在处理中", false);
            }
        }finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
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
    private String allocationResources(Order order,OrderCommodity orderCommodity) {
        if (orderCommodity == null){
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
            throw new BaseException("资源分配失败，请创建工单联系管理员");
        }else {
            return byCommodityId.getId();
        }
    }

    //续费资源
    private String renewalResources(Order order,ServerResources byCommodityId) {
         if(ObjectUtil.isNull(byCommodityId)){
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
            throw new BaseException("资源分配失败，请创建工单联系管理员");
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
                calendar.add(Calendar.MONTH, 1);
                break;
            case PaymentPeriodConstant.QUARTERLY:
                calendar.add(Calendar.MONTH, 3);
                break;
            case PaymentPeriodConstant.YEARLY:
                calendar.add(Calendar.YEAR, 1);
                break;
        }
        return calendar.getTime();
    }


    private Result<?> validStatus(String orderId, Order order) {
        //判断订单是否存在
        if (order == null|| order.getIsDeleted()!=0){
            return Result.fail("订单不存在");
        }
        String strUserId = SecurityUtils.getStrUserId();
        if (!strUserId.equals(order.getCreateUser())){
            return Result.fail("您没有权限对此订单进行操作");
        }
        //如果订单不是待支付状态，代表不用确认付款
        if (!OrderStatus.WAIT_PAY.equals(order.getStatus())){
            return Result.success(orderStatusConvert(order.getStatus()), false);
        }

        return null;
    }


}
