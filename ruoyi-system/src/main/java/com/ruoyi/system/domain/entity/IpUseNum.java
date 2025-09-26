package com.ruoyi.system.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [xray校验节点ip使用数量]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/9/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IpUseNum {

    private String webIpPort;

    private Long usageCount;
}
