package com.ruoyi.system.constant;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/14
 */
public interface RocketMqConstant {


//    -----------------------新增-topic类型---------------------------------

    //订单
    String ORDER_ADD_TOPIC = "order-topic";

    //订单延时
    String ORDER_ADD_DELAY_TOPIC = "order-delay-topic";

    //订单超时取消
    String ORDER_ADD_CANCEL_TOPIC = "order-cancel-topic";

//    -----------------------新增-订单创建时-消费者组---------------------------------

    //创建订单商品
    String CREATE_ORDER_COMMODITY = "createOrderCommodity";

    //创建订单商品资源
    String CREATE_ORDER_COMMODITY_RESOURCES = "createOrderCommodityResources";

    //创建推广记录
    String CREATE_PROMO_RECORDS = "createPromoRecords";

    //创建延时订单关闭消息
    String CREATE_ORDER_DELAY_MESSAGE = "createOrderDelayMessage";

//    -----------------------新增-退款，取消-消费者组---------------------------------

    //30分钟订单状态校验
    String ORDER_STATUS_VALID = "orderStatusValid";


    //释放商品库存
    String RELEASE_COMMODITY = "releaseCommodity";

    //释放商品库存
    String CANCEL_PROMO_RECORDS = "cancelPromoRecords";

    //退款
    String REFUND_PROCESSING = "refundProcessing";

}
