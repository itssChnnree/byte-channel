package com.ruoyi.system.pay.application.vo;

import lombok.Data;

/**
 * [查询支付订单响应VO]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Data
public class QueryPayOrderVo {

    /**
     * 返回状态码
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
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 第三方订单号（支付宝/微信）
     */
    private String apiTradeNo;

    /**
     * 支付方式
     */
    private String type;

    /**
     * 商户ID
     */
    private Integer pid;

    /**
     * 创建订单时间
     */
    private String addTime;

    /**
     * 完成交易时间
     */
    private String endTime;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品金额
     */
    private String money;

    /**
     * 支付状态（0=未支付，1=已支付）
     */
    private Integer status;

    /**
     * 业务扩展参数
     */
    private String param;

    /**
     * 支付者账号
     */
    private String buyer;

    public boolean isPaid() {
        return status != null && status == 1;
    }
}
