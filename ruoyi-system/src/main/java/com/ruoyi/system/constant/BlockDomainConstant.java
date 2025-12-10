package com.ruoyi.system.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * [屏蔽域名常量]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/12
 */
public interface BlockDomainConstant {

    List<String> LIST = Arrays.asList("domain", "full", "regexp", "geosite");

    //域名及子域名
    String DOMAIN = "domain";

    //完全匹配
    String FULL = "full";

    //正则表达式
    String REGEXP = "regexp";

    //内置数据库
    String GEOSITE = "geosite";
}
