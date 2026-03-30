package com.ruoyi.system.pay.domain.valueobject;

import lombok.Getter;

/**
 * [退款模式枚举]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/23
 */
@Getter
public enum RefundMode {
    /**
     * 用户退款：用户主动发起的退款
     */
    USER_REFUND("USER_REFUND", "用户退款"),

    /**
     * 系统超时退款：系统自动触发的退款（订单超时未支付）
     */
    SYSTEM_TIMEOUT("SYSTEM_TIMEOUT", "系统超时退款"),

    /**
     * 差错退款：用户在多个渠道同时支付，退还非支付方式的渠道
     */
    ERROR_REFUND("ERROR_REFUND", "差错退款");

    private final String code;
    private final String desc;

    RefundMode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RefundMode fromCode(String code) {
        for (RefundMode mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("未知的退款模式: " + code);
    }
}
