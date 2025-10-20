package com.ruoyi.system.messageConsumer;

import com.ruoyi.system.domain.dto.OrderMessageDto;
import com.ruoyi.system.domain.entity.Idempotency;
import com.ruoyi.system.mapper.IdempotencyMapper;
import com.ruoyi.system.mapper.OrderCommodityMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/16
 */
@Service
@Slf4j
public class OrderGeneral {

    @Resource
    private OrderCommodityMapper orderCommodityMapper;


    @Resource
    private RedissonClient redissonClient;


    @Resource
    private IdempotencyMapper idempotencyMapper;



    public void orderGeneral(OrderMessageDto orderMessageDto, String type, OrderFunction orderFunction){
        //创建类型锁
        RLock lock = redissonClient.getLock(type + ":" + orderMessageDto.getOrder().getId());
        try {
            boolean getLock = lock.tryLock(5, TimeUnit.MILLISECONDS);
            //获取分布式锁，获取到锁进入处理流程
            if (getLock){
                Integer i = idempotencyMapper.getByIdAndType(orderMessageDto.getOrder().getId(), type);
                if (i!=null){
                    log.info("订单类型[{}]已处理：{}",type, orderMessageDto.getOrder().getId());
                    return;
                }
                orderFunction.execute();
                insertIdempotency(orderMessageDto,type);
            }else {
                log.error("订单类型[{}]正在处理中：{}", type, orderMessageDto.getOrder().getId());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }

    }


    private void insertIdempotency(OrderMessageDto orderMessageDto,String type) {
        Idempotency idempotency= new Idempotency();
        idempotency.setRequestId(orderMessageDto.getOrder().getId());
        idempotency.setBusinessType(type);
        idempotency.setCreateUser(orderMessageDto.getOrder().getUserId());
        idempotency.setUpdateUser(orderMessageDto.getOrder().getUserId());
        idempotencyMapper.insert(idempotency);
    }
}
