package com.ruoyi.system.pay.application.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * [页面跳转支付请求DTO]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Data
public class PagePayRequest {

    /**
     * 商户ID（不传则使用配置文件中的默认商户ID）
     */
    private Integer pid;

    /**
     * 支付方式（不传则跳转收银台）
     */
    @Pattern(regexp = "alipay|wxpay|qqpay", message = "支付方式必须是alipay、wxpay或qqpay")
    private String type;

    /**
     * 商户订单号
     */
    @NotBlank(message = "商户订单号不能为空")
    @Size(max = 32, message = "商户订单号长度不能超过32位")
    private String outTradeNo;

    /**
     * 商品名称（最大127字节）
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 商品金额（元，最多2位小数）
     */
    @NotBlank(message = "商品金额不能为空")
    private String money;

    /**
     * 异步通知地址（覆盖默认配置）
     */
    private String notifyUrl;

    /**
     * 跳转通知地址（覆盖默认配置）
     */
    private String returnUrl;

    /**
     * 业务扩展参数（支付后原样返回）
     */
    private String param;

    /**
     * 自定义通道ID（未进件请勿传）
     */
    private Integer channelId;
}
