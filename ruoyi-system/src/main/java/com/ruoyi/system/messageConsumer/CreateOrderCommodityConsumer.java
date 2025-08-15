package com.ruoyi.system.messageConsumer;

import com.ruoyi.system.constant.RocketMqConstant;
import com.ruoyi.system.domain.dto.OrderMessageDto;
import com.ruoyi.system.domain.entity.Idempotency;
import com.ruoyi.system.domain.entity.OrderCommodity;
import com.ruoyi.system.mapper.IdempotencyMapper;
import com.ruoyi.system.mapper.OrderCommodityMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * [创建订单商品]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/13
 */
@RocketMQMessageListener(consumerGroup = RocketMqConstant.CREATE_ORDER_COMMODITY,
        topic = RocketMqConstant.ORDER_TOPIC)
@Slf4j
@Service
public class CreateOrderCommodityConsumer implements RocketMQListener<OrderMessageDto> {


    @Resource
    private OrderCommodityMapper orderCommodityMapper;


    @Resource
    private RedissonClient redissonClient;


    @Resource
    private IdempotencyMapper idempotencyMapper;


    /**
     * [创建OrderCommodity]
     * @author 陈湘岳 2025/8/15
     * @param orderMessageDto
     * @return void
     **/
    @Override
    @Transactional
    public void onMessage(OrderMessageDto orderMessageDto) {
        //创建订单商品锁
        RLock lock = redissonClient.getLock(RocketMqConstant.CREATE_ORDER_COMMODITY + ":" + orderMessageDto.getOrderByCommodityDto().getCommodityId());
        try {
            boolean getLock = lock.tryLock(5, TimeUnit.MILLISECONDS);
            //获取分布式锁，获取到锁进入处理流程
            if (getLock){
                int i = idempotencyMapper.getByIdAndType(orderMessageDto.getOrder().getId(), RocketMqConstant.CREATE_ORDER_COMMODITY);
                if (i>0){
                    log.info("订单已处理：{}", orderMessageDto.getOrder().getId());
                    return;
                }
                OrderCommodity orderCommodity = new OrderCommodity();
                orderCommodity.setOrderId(orderMessageDto.getOrder().getId());
                orderCommodity.setCommodityId(orderMessageDto.getOrderByCommodityDto().getCommodityId());
                orderCommodity.setUserId(orderMessageDto.getOrder().getUserId());
                orderCommodity.setOrderQuantity(0);
                orderCommodity.setPurchaseQuantity(orderMessageDto.getOrderByCommodityDto().getNum());
                orderCommodityMapper.insert(orderCommodity);
                Idempotency idempotency= new Idempotency();
                idempotency.setRequestId(orderMessageDto.getOrder().getId());
                idempotency.setBusinessType(RocketMqConstant.CREATE_ORDER_COMMODITY);
                idempotencyMapper.insert(idempotency);

            }else {
                log.error("订单正在处理中：{}", orderMessageDto.getOrder().getId());
                return;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            lock.unlock();
        }
    }
}
