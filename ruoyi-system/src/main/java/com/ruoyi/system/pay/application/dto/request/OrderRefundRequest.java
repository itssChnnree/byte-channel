package com.ruoyi.system.pay.application.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * [订单退款请求DTO]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/23
 */
@Data
public class OrderRefundRequest {

    /**
     * 订单ID
     */
    @NotBlank(message = "订单ID不能为空")
    private String orderId;

    /**
     * 退款模式：USER_REFUND/SYSTEM_TIMEOUT/ERROR_REFUND
     #com.ruoyi.system.pay.domain.valueobject.RefundMode
     */
    @NotBlank(message = "退款模式不能为空")
    private String refundMode;

    /**
     * 退款金额（元，最多2位小数）
     * 为null时使用订单金额计算（支付渠道扣手续费，非支付渠道全额）
     */
    private BigDecimal refundAmount;

    /**
     * 是否退款到余额
     * true：支付渠道的退款增加到余额，而不是原路退回
     * false：按正常流程退款
     */
    @NotNull(message = "是否退款到余额不能为空")
    private Boolean refundToBalance;

    /**
     * 退款原因
     */
    private String refundReason;
}
