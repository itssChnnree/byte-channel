package com.ruoyi.system.pay.application.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * [统一下单请求DTO]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Data
public class CreatePayOrderRequest {

    /**
     * 商户ID（不传则使用配置文件中的默认商户ID）
     */
    private Integer pid;

    /**
     * 接口类型：web/h5/mp/jsapi/scancode
     */
    @NotBlank(message = "接口类型不能为空")
    @Pattern(regexp = "web|h5|mp|jsapi|scancode", message = "接口类型必须是web、h5、mp、jsapi或scancode")
    private String method;

    /**
     * 支付方式：alipay/wxpay/qqpay
     */
    @NotBlank(message = "支付方式不能为空")
    @Pattern(regexp = "alipay|wxpay|qqpay", message = "支付方式必须是alipay、wxpay或qqpay")
    private String type;

    /**
     * 商户虚拟订单号
     */
    @NotBlank(message = "商户订单号不能为空")
    @Size(max = 32, message = "商户订单号长度不能超过32位")
    private String virtualOutTradeNo;

    /**
     * 商户虚拟订单号
     */
    @NotBlank(message = "商户订单号不能为空")
    @Size(max = 32, message = "商户订单号长度不能超过32位")
    private String realOutTradeNo;

    /**
     * 商品名称（最大127字节）
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 商品金额（元，最多2位小数）
     */
    @NotNull(message = "商品金额不能为空")
    private BigDecimal money;

    /**
     * 异步通知地址（覆盖默认配置）
     */
    private String notifyUrl;

    /**
     * 跳转通知地址（覆盖默认配置）
     */
    private String returnUrl;

    /**
     * 用户IP地址
     */
    @NotBlank(message = "用户IP地址不能为空")
    private String clientIp;

    /**
     * 设备类型：pc/mobile（仅通用网页支付需要）
     */
    private String device;

    /**
     * 业务扩展参数（支付后原样返回）
     */
    private String param;

    /**
     * 自定义通道ID（未进件请勿传）
     */
    private Integer channelId;

    /**
     * 被扫支付授权码（仅被扫支付需要）
     */
    private String authCode;

    /**
     * 用户Openid（仅JSAPI支付需要）
     */
    private String subOpenid;

    /**
     * 公众号AppId（仅JSAPI支付需要）
     */
    private String subAppid;
}
