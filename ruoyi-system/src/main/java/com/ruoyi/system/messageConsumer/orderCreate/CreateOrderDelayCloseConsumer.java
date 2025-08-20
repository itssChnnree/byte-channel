package com.ruoyi.system.messageConsumer.orderCreate;

import com.ruoyi.system.constant.RocketMqConstant;
import com.ruoyi.system.domain.dto.OrderMessageDto;
import com.ruoyi.system.messageConsumer.OrderGeneral;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * [创建订单延时校验的消费者]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/14
 */
@RocketMQMessageListener(consumerGroup = RocketMqConstant.CREATE_ORDER_DELAY_MESSAGE,
        topic = RocketMqConstant.ORDER_TOPIC)
@Slf4j
@Component
public class CreateOrderDelayCloseConsumer implements RocketMQListener<OrderMessageDto> {


    @Resource
    private OrderGeneral orderGeneral;


    @Resource
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(OrderMessageDto orderMessageDto) {
        orderGeneral.orderGeneral(orderMessageDto, RocketMqConstant.CREATE_ORDER_DELAY_MESSAGE, () -> {
            System.out.println("开始创建延迟订单");
            Message<OrderMessageDto> springMessage = MessageBuilder.withPayload(orderMessageDto)
                    .setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, 16)  // 设置延迟级别为16，即30分钟
                    .build();

            SendResult sendResult1 = rocketMqTemplate.syncSend(RocketMqConstant.ORDER_DELAY_TOPIC, springMessage);
            if (!sendResult1.getSendStatus().equals(SendStatus.SEND_OK)){
                SendResult sendResult2 = rocketMqTemplate.syncSend(RocketMqConstant.ORDER_DELAY_TOPIC, springMessage);
                if (!sendResult2.getSendStatus().equals(SendStatus.SEND_OK)){
                    throw new RuntimeException("订单["+orderMessageDto.getOrder().getId()+"]发送延时消息失败，等待重试");
                }
            }
        });

    }
}
