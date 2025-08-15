package com.ruoyi.system.constant;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/14
 */
public interface RocketMqConstant {

    //订单
    String ORDER_TOPIC = "order-topic";


    //订单延时
    String ORDER_DELAY_TOPIC = "order-delay-topic";

//    -----------------------消费者组---------------------------------

    //创建订单商品
    String CREATE_ORDER_COMMODITY_GROUP = "create-order-commodity-group";

    //创建延时消息
    String CREATE_ORDER_DELAY_MESSAGE = "create-order-delay-message";
}
