package com.ruoyi.system.domain.dto.log;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * [监控数据入参]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2024/3/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("监控数据入参")
public class MonitorLogDTO {


    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("查询索引")
    @NotNull(message = "请选择需要操作的索引")
    private String index;

    @ApiModelProperty("查询字段")
    @NotNull(message = "请输入需要查询的键")
    private String fieldKey;

    @ApiModelProperty("查询条件")
    @NotNull(message = "请输入需要查询的值")
    private String fieldValue;

    @ApiModelProperty("需要查询的指标")
    @NotNull(message = "请输入需要查询的指标")
    private String metric;

    @ApiModelProperty("时间间隔，0为15秒，1为秒，2为分钟，3为小时，4为天，5为周，6为月，7为季度，8为年")
    private int timeGroupType;
}
