package com.ruoyi.system.pay.domain.valueobject;

import lombok.Getter;

/**
 * [支付状态枚举]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Getter
public enum PayStatus {
    WAIT_PAY(0, "待支付"),
    TRADE_SUCCESS(1, "支付成功"),
    TRADE_CLOSED(2, "交易关闭"),
    REFUNDING(3, "退款中"),
    REFUND_SUCCESS(4, "退款成功");

    private final int code;
    private final String desc;

    PayStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PayStatus fromCode(int code) {
        for (PayStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的支付状态: " + code);
    }
}
