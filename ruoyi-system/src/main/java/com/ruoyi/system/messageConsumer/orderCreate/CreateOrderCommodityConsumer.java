package com.ruoyi.system.messageConsumer.orderCreate;

import com.ruoyi.system.constant.RocketMqConstant;
import com.ruoyi.system.domain.dto.OrderMessageDto;
import com.ruoyi.system.domain.entity.OrderCommodity;
import com.ruoyi.system.mapper.OrderCommodityMapper;
import com.ruoyi.system.messageConsumer.OrderGeneral;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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
    private OrderGeneral orderGeneral;

    /**
     * [创建OrderCommodity]
     * @author 陈湘岳 2025/8/15
     * @param orderMessageDto
     * @return void
     **/
    @Override
    @Transactional
    public void onMessage(OrderMessageDto orderMessageDto) {
        orderGeneral.orderGeneral(orderMessageDto, RocketMqConstant.CREATE_ORDER_COMMODITY, () -> {
            //查入商品订单数据
            insertOrderCommodity(orderMessageDto);
        });
    }



    private void insertOrderCommodity(OrderMessageDto orderMessageDto) {
        OrderCommodity orderCommodity = new OrderCommodity();
        orderCommodity.setOrderId(orderMessageDto.getOrder().getId());
        orderCommodity.setCommodityId(orderMessageDto.getOrderByCommodityDto().getCommodityId());
        orderCommodity.setUserId(orderMessageDto.getOrder().getUserId());
        orderCommodity.setOrderQuantity(0);
        orderCommodity.setPurchaseQuantity(orderMessageDto.getOrderByCommodityDto().getNum());
        orderCommodity.setCreateUser(orderMessageDto.getOrder().getUserId());
        orderCommodity.setUpdateUser(orderMessageDto.getOrder().getUserId());
        orderCommodityMapper.insert(orderCommodity);
    }
}
