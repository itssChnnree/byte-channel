package com.ruoyi.system.pay.domain.valueobject;

import lombok.Getter;

/**
 * [接口类型枚举]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Getter
public enum PayMethod {
    WEB("web", "通用网页支付"),
    H5("h5", "H5支付"),
    MP("mp", "小程序支付"),
    JSAPI("jsapi", "公众号支付"),
    SCANCODE("scancode", "被扫支付");

    private final String code;
    private final String desc;

    PayMethod(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PayMethod fromCode(String code) {
        for (PayMethod method : values()) {
            if (method.code.equals(code)) {
                return method;
            }
        }
        throw new IllegalArgumentException("未知的接口类型: " + code);
    }
}
