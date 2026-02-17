package com.ruoyi.system.domain.dto.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [监控性能查询响应]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2024/3/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricDTO {


    private String time;

    private Double metric;
}
