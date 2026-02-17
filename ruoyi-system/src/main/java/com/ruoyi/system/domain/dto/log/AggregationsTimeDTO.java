package com.ruoyi.system.domain.dto.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [按时间聚合存储时间和聚合数]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2024/2/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregationsTimeDTO {

    /**
     * 时间
     * */
    private String time;

    /**
     * 聚合数
     * */
    private long count;
}
