package com.ruoyi.system.pay.domain.valueobject;

import lombok.Getter;

/**
 * [设备类型枚举]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Getter
public enum DeviceType {
    PC("pc", "电脑浏览器"),
    MOBILE("mobile", "手机浏览器");

    private final String code;
    private final String desc;

    DeviceType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DeviceType fromCode(String code) {
        for (DeviceType device : values()) {
            if (device.code.equals(code)) {
                return device;
            }
        }
        throw new IllegalArgumentException("未知的设备类型: " + code);
    }
}
