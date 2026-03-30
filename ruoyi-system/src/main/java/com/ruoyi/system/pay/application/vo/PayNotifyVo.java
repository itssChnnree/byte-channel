package com.ruoyi.system.pay.application.vo;

import lombok.Data;

/**
 * [支付通知VO]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Data
public class PayNotifyVo {

    /**
     * 商户ID
     */
    private Integer pid;

    /**
     * 平台订单号
     */
    private String tradeNo;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 支付方式
     */
    private String type;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品金额
     */
    private String money;

    /**
     * 支付状态（TRADE_SUCCESS=成功）
     */
    private String tradeStatus;

    /**
     * 业务扩展参数
     */
    private String param;

    /**
     * 时间戳（10位秒级）
     */
    private String timestamp;

    /**
     * 签名字符串
     */
    private String sign;

    /**
     * 签名类型
     */
    private String signType;

    public boolean isTradeSuccess() {
        return "TRADE_SUCCESS".equals(tradeStatus);
    }
}
