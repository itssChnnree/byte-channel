package com.ruoyi.system.pay.application.vo;

import lombok.Data;

/**
 * [创建支付订单响应VO]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Data
public class CreatePayOrderVo {

    /**
     * 返回状态码（1=成功）
     */
    private Integer code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 平台订单号
     */
    private String tradeNo;

    /**
     * 支付跳转URL（如返回则直接跳转）
     */
    private String payUrl;

    /**
     * 二维码链接（如返回则生成二维码）
     */
    private String qrCode;

    /**
     * 小程序跳转URL
     */
    private String urlScheme;

    public boolean isSuccess() {
        return code != null && code == 1;
    }
}
