package com.ruoyi.system.pay.strategy.v2;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.system.pay.PayFeeRateConfig;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * [V2支付平台配置类 - 佳梦云付]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/4/6
 */
@Data
@Component("v2PayProperties")
@ConfigurationProperties(prefix = "pay.platform.v2")
@ConditionalOnProperty(name = "pay.platform.version", havingValue = "v2", matchIfMissing = true)
public class V2PayProperties implements PayFeeRateConfig {

    /** 支付平台基础URL */
    private String baseUrl = "https://pay.avuoo.com";

    /** 商户ID */
    private Integer pid;

    /** 商户私钥（PEM格式，用于请求签名） */
    private String merchantPrivateKey;

    /** 平台公钥（PEM格式，用于响应验签） */
    private String platformPublicKey;

    /** 异步通知地址 */
    private String notifyUrl;

    /** 同步跳转地址 */
    private String returnUrl;

    /** 签名类型（V2固定为RSA） */
    private String signType = "RSA";

    /** 连接超时时间（毫秒） */
    private int connectTimeout = 5000;

    /** 读取超时时间（毫秒） */
    private int readTimeout = 10000;

    /** 微信手续费比例 */
    private BigDecimal wechatFeeRate = new BigDecimal("0.07");

    /** 支付宝手续费比例 */
    private BigDecimal alipayFeeRate = new BigDecimal("0.07");

    @Resource
    private RedisCache redisCache;

    /**
     * [初始化后从Redis读取手续费比例覆盖默认值]
     *
     * @author 陈湘岳 2026/4/6
     **/
    @PostConstruct
    public void initFeeRates() {
        // 支付宝手续费
        String alipayFee = redisCache.getCacheObject("sys_config:sys:refoundAli:fee");
        if (StrUtil.isNotBlank(alipayFee)) {
            try {
                this.alipayFeeRate = new BigDecimal(alipayFee);
            } catch (NumberFormatException ignored) {
            }
        }
        // 微信手续费
        String wechatFee = redisCache.getCacheObject("sys_config:sys:refoundWx:fee");
        if (StrUtil.isNotBlank(wechatFee)) {
            try {
                this.wechatFeeRate = new BigDecimal(wechatFee);
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
