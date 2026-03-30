package com.ruoyi.system.pay.application.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * [退款结果VO]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResultVo {

    /**
     * 是否退款成功
     */
    private Boolean success;

    /**
     * 退款总金额
     */
    private BigDecimal totalRefundAmount;

    /**
     * 各渠道退款详情
     */
    private List<ChannelRefundResult> channelResults;

    /**
     * 退款消息
     */
    private String message;

    /**
     * 渠道退款结果
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChannelRefundResult {
        /**
         * 支付渠道：WECHAT_PAY/ALIPAY_PAY/BALANCE_PAY
         */
        private String channel;

        /**
         * 虚拟订单号（第三方支付）
         */
        private String virtualOrderId;

        /**
         * 退款金额
         */
        private BigDecimal refundAmount;

        /**
         * 是否成功
         */
        private Boolean success;

        /**
         * 结果消息
         */
        private String message;
    }
}
