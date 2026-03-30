package com.ruoyi.system.pay.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * [支付平台配置类]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Data
@Component
@ConfigurationProperties(prefix = "pay.platform")
public class PayProperties {

    /**
     * 支付平台基础URL
     */
    private String baseUrl = "https://pay.avuoo.com";

    /**
     * 商户ID
     */
    private Integer pid;

    /**
     * 商户私钥（PEM格式，用于请求签名）
     */
    private String merchantPrivateKey;

    /**
     * 平台公钥（PEM格式，用于响应验签）
     */
    private String platformPublicKey;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 同步跳转地址
     */
    private String returnUrl;

    /**
     * 签名类型（V2固定为RSA）
     */
    private String signType = "RSA";

    /**
     * 连接超时时间（毫秒）
     */
    private int connectTimeout = 5000;

    /**
     * 读取超时时间（毫秒）
     */
    private int readTimeout = 10000;

    /**
     * 微信手续费比例（0.07表示7%）
     */
    private BigDecimal wechatFeeRate = new BigDecimal("0.07");

    /**
     * 支付宝手续费比例（0.07表示7%）
     */
    private BigDecimal alipayFeeRate = new BigDecimal("0.07");
}
