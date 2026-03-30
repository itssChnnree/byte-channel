package com.ruoyi.system.pay.domain.valueobject;

import lombok.Getter;

/**
 * [支付方式枚举]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Getter
public enum PayType {
    ALIPAY("alipay", "支付宝"),
    WECHAT_PAY("wxpay", "微信支付"),
    QQ_PAY("qqpay", "QQ钱包");

    private final String code;
    private final String desc;

    PayType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PayType fromCode(String code) {
        for (PayType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的支付方式: " + code);
    }
}
