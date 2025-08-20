package com.ruoyi.system.messageConsumer.orderCreate;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.system.constant.RocketMqConstant;
import com.ruoyi.system.domain.dto.OrderMessageDto;
import com.ruoyi.system.mapper.OrderCommodityMapper;
import com.ruoyi.system.messageConsumer.OrderGeneral;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * [创建订单推广记录]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/15
 */
@RocketMQMessageListener(consumerGroup = RocketMqConstant.CREATE_PROMO_RECORDS,
        topic = RocketMqConstant.ORDER_TOPIC)
@Slf4j
@Component
public class CreatePromoRecordsConsumer implements RocketMQListener<OrderMessageDto> {


    @Resource
    private OrderCommodityMapper orderCommodityMapper;


    @Resource
    private OrderGeneral orderGeneral;

    @Override
    public void onMessage(OrderMessageDto orderMessageDto) {
        if(ObjectUtil.isEmpty(orderMessageDto.getPromoCodeRecordsDto())){
            log.error("订单推广记录为空：{}", orderMessageDto.getOrder().getId());
            return;
        }
        orderGeneral.orderGeneral(orderMessageDto, RocketMqConstant.CREATE_PROMO_RECORDS, () -> {
            System.out.println("orderGeneral");
        });

    }
}
