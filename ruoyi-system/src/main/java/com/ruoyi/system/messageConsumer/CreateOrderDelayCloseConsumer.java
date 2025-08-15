package com.ruoyi.system.messageConsumer;

import com.ruoyi.system.constant.RocketMqConstant;
import com.ruoyi.system.domain.dto.OrderMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * [创建订单延时关闭消息的消费者]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/14
 */
@RocketMQMessageListener(consumerGroup = RocketMqConstant.ORDER_DELAY_TOPIC,
        topic = RocketMqConstant.ORDER_TOPIC)
@Slf4j
@Component
public class CreateOrderDelayCloseConsumer implements RocketMQListener<OrderMessageDto> {


    @Override
    public void onMessage(OrderMessageDto orderMessageDto) {

    }
}
