package com.ruoyi.system.pay.application.dto.request;

import lombok.Data;

/**
 * [查询支付订单请求DTO]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Data
public class QueryPayOrderRequest {

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
}
