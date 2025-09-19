package com.ruoyi.system.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.util.DefaultValueUtil;
import com.ruoyi.system.constant.OrderStatus;
import com.ruoyi.system.constant.PaymentPeriodConstant;
import com.ruoyi.system.constant.RedissonLockStatus;
import com.ruoyi.system.constant.RocketMqConstant;
import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.dto.OrderDto;
import com.ruoyi.system.domain.dto.OrderMessageDto;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.domain.vo.OrderInfoVo;
import com.ruoyi.system.domain.vo.OrderVo;
import com.ruoyi.system.domain.vo.PriceCalculateVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IOrderService;
import com.ruoyi.system.strategy.OrderCreate.OrderCreateContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

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
    private RocketMQTemplate rocketMqTemplate;

    @Resource
    private PromoCodeRecordsMapper promoCodeRecordsMapper;

    @Resource
    private PromoRecordsMapper  promoRecordsMapper;

    @Resource(name = "orderCache")
    private Cache<String, ReentrantLock> orderCache;


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
        try {
            DefaultValueUtil.setDefaultData(orderByCommodityDto);
        } catch (IllegalAccessException e) {
            log.error("默认赋值失败"+e.getMessage());
            throw new RuntimeException("默认赋值失败");
        }
        RLock lock = redissonClient.getLock(RedissonLockStatus.COMMODITY_STOCK_CHANGE_LOCK + orderByCommodityDto.getCommodityId());
        try{
            //变更商品库存
            updateCommodityStock(orderByCommodityDto, lock, normalCommodity);
        }catch (InterruptedException e){
            log.error("获取锁异常", e);
            throw new RuntimeException("系统繁忙，请稍后再试");
        }finally {
            lock.unlock();
        }
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
        OrderMessageDto orderMessageDto = buildOrderMessageDto(orderByCommodityDto, order, promoCodeRecords);
        insertOrderCommodity(orderMessageDto);
        insertPromoRecords(orderMessageDto);
        //发送消息，若
        sendMessage(orderMessageDto);
        return Result.success(order);
    }


    private void insertOrderCommodity(OrderMessageDto orderMessageDto) {
        OrderCommodity orderCommodity = new OrderCommodity();
        orderCommodity.setOrderId(orderMessageDto.getOrder().getId());
        orderCommodity.setCommodityId(orderMessageDto.getOrderByCommodityDto().getCommodityId());
        orderCommodity.setUserId(orderMessageDto.getOrder().getUserId());
        orderCommodity.setOrderQuantity(0);
        orderCommodity.setPurchaseQuantity(orderMessageDto.getOrderByCommodityDto().getNum());
        orderCommodity.setCreateUser(orderMessageDto.getOrder().getUserId());
        orderCommodity.setUpdateUser(orderMessageDto.getOrder().getUserId());
        orderCommodityMapper.insert(orderCommodity);
    }


    private void insertPromoRecords(OrderMessageDto orderMessageDto) {
        PromoCodeRecords promoCodeRecordsDto = orderMessageDto.getPromoCodeRecordsDto();
        if (promoCodeRecordsDto== null){
            return;
        }
        PromoRecords promoRecords = new PromoRecords();
        promoRecords.setUserId(promoCodeRecordsDto.getUserId());
        promoRecords.setReferralsUserId(SecurityUtils.getStrUserId());
        promoRecords.setPromoCodeRecordsId(promoCodeRecordsDto.getId());
        promoRecords.setOrderId(orderMessageDto.getOrder().getId());
        BigDecimal amount = orderMessageDto.getOrder().getAmount();
        promoRecords.setCashbackAmount(amount.multiply(new BigDecimal("0.1")));
        promoRecords.setPurchaseAmount(amount);
        promoRecords.setStatus(OrderStatus.WAIT_CONFIRM);
        promoRecords.setCashbackPercentage("10%");
        promoRecordsMapper.insert(promoRecords);
    }



    private void updateCommodityStock(OrderByCommodityDto orderByCommodityDto, RLock lock, Commodity normalCommodity) throws InterruptedException {
        boolean lockStatus = lock.tryLock(5, TimeUnit.SECONDS);
        if (lockStatus){
            Boolean commodityStockStatus = checkCommodityStock(orderByCommodityDto, normalCommodity);
            if (!commodityStockStatus){
                throw new RuntimeException("商品库存不足");
            }
            commodityMapper.update(normalCommodity);
        }else {
            throw new RuntimeException("系统繁忙，请稍后再试");
        }
    }


    private void sendMessage(OrderMessageDto orderMessageDto) {
        SendResult sendResult1 = rocketMqTemplate.syncSend(RocketMqConstant.ORDER_ADD_TOPIC, orderMessageDto);
        if (!sendResult1.getSendStatus().equals(SendStatus.SEND_OK)){
            SendResult sendResult2 = rocketMqTemplate.syncSend(RocketMqConstant.ORDER_ADD_TOPIC, orderMessageDto);
            if (!sendResult2.getSendStatus().equals(SendStatus.SEND_OK)){
                throw new RuntimeException("生成订单失败，请稍后再试");
            }
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


    //校验商品库存
    private Boolean checkCommodityStock(OrderByCommodityDto orderByCommodityDto, Commodity normalCommodity) {
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
        Page<Order> page = new Page<>(orderDto.getPageNum(), orderDto.getPageSize());
        IPage<OrderVo> orderVoIPage = orderMapper.queryPage(page, orderDto, SecurityUtils.getStrUserId());
        return Result.success(orderVoIPage);
    }


    /**
     * [消息队列测试]
     *
     * @return java.lang.String
     * @author 陈湘岳 2025/8/14
     **/
    @Override
    public String test() {
//        for (int i = 0; i < 300; i++){
            OrderMessageDto orderMessageDto = new OrderMessageDto(20000, null, null, null);
            SendResult sendResult1 = rocketMqTemplate.syncSend(RocketMqConstant.ORDER_ADD_TOPIC, orderMessageDto);
//        }
        return "测试完毕";
    }

    /**
     * [取消订单]
     *
     * @param orderId
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/24
     **/
    @Override
    public Result cancelOrder(String orderId) {
        Order order = orderMapper.queryById(orderId);
        if (order == null){
            return Result.fail("订单不存在");
        }
        if (OrderStatus.WAIT_REFUND.equals(order.getStatus())){
            return Result.fail("订单正在退款中");
        }
        if (OrderStatus.USER_CANCELED.equals(order.getStatus())
                || OrderStatus.CANCELED_TIMEOUT.equals(order.getStatus())){
            return Result.fail("订单已取消");
        }
        if (OrderStatus.REFUND_SUCCESS.equals(order.getStatus())){
            return Result.fail("订单已退款成功");
        }
        //订单取消标志位
        String key = RedissonLockStatus.ORDER_CANCEL_STATUS_LOCK+orderId;
        //插入成功，说明订单未撤回，插入失败代表订单已撤回
        Boolean isNotCancel = redisTemplate.opsForValue().setIfAbsent(key, "1",3600, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(isNotCancel)){
            //订单撤回标志位设置成功
            //设置过期时间
            order.setStatus(OrderStatus.USER_CANCELED);
            orderMapper.updateById(order);
            OrderByCommodityDto commodityByOrderId = orderCommodityMapper.findCommodityByOrderId(orderId);
            OrderMessageDto orderMessageDto = buildOrderMessageDto(commodityByOrderId, order, null);
            SendResult sendResult1 = rocketMqTemplate.syncSend(RocketMqConstant.ORDER_ADD_CANCEL_TOPIC, orderMessageDto);
            if (!sendResult1.getSendStatus().equals(SendStatus.SEND_OK)){
                SendResult sendResult2 = rocketMqTemplate.syncSend(RocketMqConstant.ORDER_ADD_CANCEL_TOPIC, orderMessageDto);
                if (!sendResult2.getSendStatus().equals(SendStatus.SEND_OK)){
                    throw new RuntimeException("订单["+orderMessageDto.getOrder().getId()+"]发送取消消息失败，等待重试");
                }
            }
            return Result.success("订单取消成功");
        }else {
            return Result.fail("订单已取消");
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
        try {
            DefaultValueUtil.setDefaultData(orderByCommodityDto);
        } catch (IllegalAccessException e) {
            log.error("默认赋值失败" + e.getMessage());
            throw new RuntimeException("默认赋值失败");
        }

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
}
