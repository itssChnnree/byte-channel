package com.ruoyi.system.messageConsumer.orderCancel;

import com.ruoyi.system.constant.RedissonLockStatus;
import com.ruoyi.system.constant.RocketMqConstant;
import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.dto.OrderMessageDto;
import com.ruoyi.system.domain.entity.Commodity;
import com.ruoyi.system.mapper.CommodityMapper;
import com.ruoyi.system.mapper.OrderCommodityMapper;
import com.ruoyi.system.messageConsumer.OrderGeneral;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * [释放商品占用]
 *
 * @author
 * @version v1.0.0
 * @date 2025/8/19
 */
@RocketMQMessageListener(consumerGroup = RocketMqConstant.RELEASE_COMMODITY,
        topic = RocketMqConstant.ORDER_CANCEL_TOPIC)
@Slf4j
@Service
public class ReleaseCommodityConsumer implements RocketMQListener<OrderMessageDto> {


    @Resource
    private OrderGeneral orderGeneral;

    @Resource
    private CommodityMapper commodityMapper;

    @Resource
    private OrderCommodityMapper orderCommodityMapper;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void onMessage(OrderMessageDto orderMessageDto) {
        orderGeneral.orderGeneral(orderMessageDto, RocketMqConstant.RELEASE_COMMODITY, () -> {
            //获取商品锁
            RLock lock = redissonClient.getLock(RedissonLockStatus.COMMODITY_STOCK_CHANGE_LOCK + orderMessageDto.getOrderByCommodityDto().getCommodityId());
            try{
                boolean lockStatus = lock.tryLock(5, TimeUnit.SECONDS);
                if (lockStatus){
                    Commodity commodity = commodityMapper.findNormalCommodity(orderMessageDto.getOrderByCommodityDto().getCommodityId());
                    OrderByCommodityDto orderByCommodityDto = orderMessageDto.getOrderByCommodityDto();

                }else {
                    throw new RuntimeException("系统繁忙，请稍后再试");
                }
            }catch (InterruptedException e){
                log.error("获取锁异常", e);
                throw new RuntimeException("系统繁忙，请稍后再试");
            }finally {
                lock.unlock();
            }
        });
    }

    //获取新库存
    private void updateInventory(Commodity commodity,OrderByCommodityDto orderByCommodityDto){

    }
}
