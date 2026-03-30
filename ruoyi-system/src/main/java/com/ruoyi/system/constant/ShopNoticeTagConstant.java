package com.ruoyi.system.constant;

import java.util.Arrays;
import java.util.List;

/**
 * [文档标签]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/26
 */
public interface ShopNoticeTagConstant {


    // 常量定义
    String CLIENT_CONFIGURATION = "客户端配置";
    String USAGE_TIPS = "使用技巧";
    String FREQUENTLY_ASKED_QUESTIONS = "常见问题";

    // 常量列表（不可变）
    public static final List<String> SECTIONS = Arrays.asList(
            CLIENT_CONFIGURATION,
            USAGE_TIPS,
            FREQUENTLY_ASKED_QUESTIONS
    );

}
