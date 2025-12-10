package com.ruoyi.system.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * [频闭域名数组]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/12/8
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlackDomainArr {

    private List<String> domains;
}
