package com.ruoyi.system.constant;

import java.util.List;

/**
 * [实体状态]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/12
 */
public interface EntityStatus {

    //正常
    String NORMAL = "NORMAL";

    //禁用
    String DISABLED = "DISABLED";

    //已处理
    String HANDLED = "HANDLED";

    //未处理
    String UNHANDLED = "UNHANDLED";

    //待删除
    String TO_BE_DELETED = "TO_BE_DELETED";

    //待生效
    String TO_BE_EFFECTIVE = "TO_BE_EFFECTIVE";

    //待失效
    String TO_BE_INVALID = "TO_BE_INVALID";

    List<String> NORMAL_LIST = List.of(NORMAL, TO_BE_EFFECTIVE);
}
