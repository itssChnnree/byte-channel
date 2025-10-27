package com.ruoyi.system.constant;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/12
 */
public interface OrderStatus {

    //-----------------------------------------------推广状态--------------------------------------------------------------------

    //待确认
    String WAIT_CONFIRM = "WAIT_CONFIRM";

    //已返现
    String RETURN_CASH = "RETURN_CASH";

    //已退款
    String REFUND = "REFUND";

    //已取消
    String CANCELED = "CANCELED";

    //-----------------------------------------------订单状态--------------------------------------------------------------------

    // 待支付
     String WAIT_PAY = "WAIT_PAY";

    //待分配资源
     String WAIT_ALLOCATION_RESOURCES = "WAIT_ALLOCATION_RESOURCES";

    //已完成
     String COMPLETED = "COMPLETED";

    //已取消 用户主动取消
     String USER_CANCELED = "USER_CANCELED";

    //已取消 超时系统取消
     String CANCELED_TIMEOUT = "CANCELED_TIMEOUT";

    //待退款
     String WAIT_REFUND = "WAIT_REFUND";

    //退款成功
     String REFUND_SUCCESS = "REFUND_SUCCESS";

    //-----------------------------------------------支付方式--------------------------------------------------------------------

    //支付宝扫码支付
    String ALIPAY_PAY = "ALIPAY_PAY";

    //微信扫码支付
     String WECHAT_PAY = "WECHAT_PAY";

    //余额支付
     String BALANCE_PAY = "BALANCE_PAY";
}
