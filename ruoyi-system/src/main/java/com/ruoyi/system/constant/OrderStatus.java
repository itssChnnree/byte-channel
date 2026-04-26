package com.ruoyi.system.constant;

import java.util.Arrays;
import java.util.List;

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

    //已退款
     String REFUND_SUCCESS = "REFUND_SUCCESS";

    //差错退款订单状态集合
    List<String> ERROR_REFUND_JOB_STATUS_LIST = Arrays.asList(USER_CANCELED,CANCELED_TIMEOUT,WAIT_ALLOCATION_RESOURCES,COMPLETED);
    //-----------------------------------------------支付方式--------------------------------------------------------------------

    //支付宝支付
    String ALIPAY_PAY = "ALIPAY_PAY";

    //微信支付
     String WECHAT_PAY = "WECHAT_PAY";

    //余额支付
     String BALANCE_PAY = "BALANCE_PAY";
}
