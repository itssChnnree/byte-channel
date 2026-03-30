package com.ruoyi.system.pay.application.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * [订单退款请求DTO]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Data
public class RefundPayOrderRequest {

    /**
     * 商户ID（不传则使用配置文件中的默认商户ID）
     */
    private Integer pid;

    /**
     * 平台订单号（与outTradeNo二选一）
     */
    private String tradeNo;

    /**
     * 商户订单号（与tradeNo二选一）
     */
    private String outTradeNo;

    /**
     * 退款金额（元，最多2位小数）
     */
    @NotNull(message = "退款金额不能为空")
    private BigDecimal money;
}
