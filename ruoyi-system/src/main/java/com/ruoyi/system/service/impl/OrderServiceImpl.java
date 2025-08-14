package com.ruoyi.system.service.impl;


import com.ruoyi.system.constant.RedissonLockStatus;
import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.dto.OrderMessageDto;
import com.ruoyi.system.domain.entity.Commodity;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.entity.PromoCodeRecords;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.CommodityMapper;
import com.ruoyi.system.mapper.OrderMapper;
import com.ruoyi.system.mapper.PromoCodeRecordsMapper;
import com.ruoyi.system.service.IOrderService;
import com.ruoyi.system.strategy.OrderCreate.OrderCreateContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

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
    private RocketMQTemplate rocketMqTemplate;

    @Resource
    private PromoCodeRecordsMapper promoCodeRecordsMapper;


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
        RLock lock = redissonClient.getLock(RedissonLockStatus.COMMODITY_STOCK_CHANGE_LOCK + orderByCommodityDto.getCommodityId());
        try{
            boolean lockStatus = lock.tryLock(5, TimeUnit.SECONDS);
            if (lockStatus){
                Boolean commodityStockStatus = checkCommodityStock(orderByCommodityDto, normalCommodity);
                if (!commodityStockStatus){
                    return Result.fail("商品库存不足");
                }
                commodityMapper.update(normalCommodity);
            }else {
                return Result.fail("系统繁忙，请稍后再试");
            }
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
        //订单入库
        int insert = orderMapper.insert(order);
        if (insert<1){
            throw new RuntimeException("生成订单失败，请稍后再试");
        }
        //创建消息队列类
        OrderMessageDto orderMessageDto = new OrderMessageDto();
        orderMessageDto.setOrder(order);
        orderMessageDto.setOrderByCommodityDto(orderByCommodityDto);
        orderMessageDto.setPromoCodeRecordsDto(promoCodeRecords);

        //消息队列： 邮箱发送
        SendResult sendResult1 = rocketMqTemplate.syncSend("order-topic", orderMessageDto);
        if (!sendResult1.getSendStatus().equals(SendStatus.SEND_OK)){
            SendResult sendResult2 = rocketMqTemplate.syncSend("order-topic", orderMessageDto);
            if (!sendResult2.getSendStatus().equals(SendStatus.SEND_OK)){
                throw new RuntimeException("生成订单失败，请稍后再试");
            }
        }
        // 设置延时级别为16 (对应30分钟)
        int delayLevel = 16;

        //延迟队列   15分钟后提醒充值  30分钟后是否支付，未支付关闭订单
        // 发送延时消息

        Message<OrderMessageDto> message = MessageBuilder.withPayload(orderMessageDto).build();
        rocketMqTemplate.syncSend("order-delay-topic", message, 3000, delayLevel);
        return Result.success(order);
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


}
