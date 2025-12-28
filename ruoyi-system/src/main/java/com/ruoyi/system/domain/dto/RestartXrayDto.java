package com.ruoyi.system.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/9/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestartXrayDto {

    private String dest;

    private List<String> serverNames;

    private Integer port;

    private String userId;

    private List<String> domains;
}
