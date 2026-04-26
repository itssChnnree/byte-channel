package com.ruoyi.system.pay;

import java.math.BigDecimal;

/**
 * 支付费率配置接口，供 V1/V2 PayProperties 实现，用于统一注入
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/4/6
 */
public interface PayFeeRateConfig {

    BigDecimal getWechatFeeRate();

    void setWechatFeeRate(BigDecimal feeRate);

    BigDecimal getAlipayFeeRate();

    void setAlipayFeeRate(BigDecimal feeRate);

}
