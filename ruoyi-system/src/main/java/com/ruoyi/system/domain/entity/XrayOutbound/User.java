package com.ruoyi.system.domain.entity.XrayOutbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/9/24
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private String id;
    private int alterId;
    private String email;
    private String security;
    private String encryption;
    private String flow;
}
