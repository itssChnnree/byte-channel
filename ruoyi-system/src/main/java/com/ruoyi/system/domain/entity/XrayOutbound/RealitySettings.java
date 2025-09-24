package com.ruoyi.system.domain.entity.XrayOutbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/9/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RealitySettings {
    private String serverName;
    private String fingerprint;
    private boolean show;
    private String publicKey;
    private String shortId;
    private String spiderX;
}
