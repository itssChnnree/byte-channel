package com.ruoyi.system.domain.dto.log;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * [时间聚合返回类]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2024/2/26
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("用户操作日志查询入参")
public class AggregationDTO {

    //存储hits中数据
    private List<Object> hit;

    private int size;

    private int page;

    private long total;
}
