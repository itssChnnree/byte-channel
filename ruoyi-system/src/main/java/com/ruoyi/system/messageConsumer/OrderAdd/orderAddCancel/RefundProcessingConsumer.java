package com.ruoyi.system.messageConsumer.OrderAdd.orderAddCancel;

/**
 * [退款！]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/24
 */

import com.ruoyi.system.constant.RocketMqConstant;
import com.ruoyi.system.domain.dto.OrderMessageDto;
import com.ruoyi.system.messageConsumer.OrderGeneral;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * [退款]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/23
 */
//todo 暂时注释增加重启速度
@RocketMQMessageListener(consumerGroup = RocketMqConstant.REFUND_PROCESSING,
        topic = RocketMqConstant.ORDER_ADD_CANCEL_TOPIC)
@Slf4j
@Service
public class RefundProcessingConsumer implements RocketMQListener<OrderMessageDto> {

    @Resource
    private OrderGeneral orderGeneral;

    @Override
    public void onMessage(OrderMessageDto orderMessageDto) {
        orderGeneral.orderGeneral(orderMessageDto, RocketMqConstant.CANCEL_PROMO_RECORDS, () -> {
            //todo 根据订单id查询第三方支付平台是否付款，付款则进行退款操作
        });
    }
}
