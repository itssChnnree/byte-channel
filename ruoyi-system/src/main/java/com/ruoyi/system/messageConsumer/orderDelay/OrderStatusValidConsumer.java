package com.ruoyi.system.messageConsumer.orderDelay;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.system.constant.OrderStatus;
import com.ruoyi.system.constant.RocketMqConstant;
import com.ruoyi.system.domain.dto.OrderMessageDto;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.mapper.OrderMapper;
import com.ruoyi.system.messageConsumer.OrderGeneral;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * [订单状态校验消费者，用于在30分钟后校验订单是否是完成状态]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/19
 */
@RocketMQMessageListener(consumerGroup = RocketMqConstant.ORDER_STATUS_VALID,
        topic = RocketMqConstant.ORDER_DELAY_TOPIC)
@Slf4j
@Service
public class OrderStatusValidConsumer implements RocketMQListener<OrderMessageDto> {


    @Resource
    private OrderGeneral orderGeneral;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private RocketMQTemplate rocketMqTemplate;

    /**
     * [创建OrderCommodity]
     * @author 陈湘岳 2025/8/15
     * @param orderMessageDto
     * @return void
     **/
    @Override
    @Transactional
    public void onMessage(OrderMessageDto orderMessageDto) {
        orderGeneral.orderGeneral(orderMessageDto, RocketMqConstant.ORDER_STATUS_VALID, () -> {
            System.out.println("当前时间"+ LocalDateTime.now());
            System.out.println("订单创建时间"+ orderMessageDto.getOrder().getCreateTime());
            Order order = orderMapper.queryById(orderMessageDto.getOrder().getId());
            if (ObjectUtil.isEmpty( order)){
                return;
            }
            //判断订单是否是完成状态
            if (OrderStatus.WAIT_PAY.equals(order.getStatus())){
                //订单置为已取消状态
                order.setStatus(OrderStatus.CANCELED_TIMEOUT);
                orderMapper.updateById(order);
                //若是待支付状态则取消订单并回退锁定资源
                SendResult sendResult1 = rocketMqTemplate.syncSend(RocketMqConstant.ORDER_CANCEL_TOPIC, orderMessageDto);
                if (!sendResult1.getSendStatus().equals(SendStatus.SEND_OK)){
                    SendResult sendResult2 = rocketMqTemplate.syncSend(RocketMqConstant.ORDER_CANCEL_TOPIC, orderMessageDto);
                    if (!sendResult2.getSendStatus().equals(SendStatus.SEND_OK)){
                        throw new RuntimeException("订单["+orderMessageDto.getOrder().getId()+"]发送取消消息失败，等待重试");
                    }
                }
            }
        });
    }
}
