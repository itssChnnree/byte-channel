package com.ruoyi.system.messageConsumer;

import com.ruoyi.system.constant.RocketMqConstant;
import com.ruoyi.system.domain.dto.OrderMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * [创建订单商品]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/13
 */
@RocketMQMessageListener(consumerGroup = RocketMqConstant.CREATE_ORDER_COMMODITY_GROUP,
        topic = RocketMqConstant.ORDER_TOPIC)
@Slf4j
@Service
public class CreateOrderCommodityConsumer implements RocketMQListener<OrderMessageDto> {





    @Override
    public void onMessage(OrderMessageDto orderMessageDto) {

    }
}
