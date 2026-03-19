package com.ruoyi.system.domain.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 服务器资源故障告警表(ServerResourceAlarm)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-18 23:39:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerResourceAlarmVo {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("服务器资源id")
    private String resourceId;

    @ApiModelProperty("告警类型")
    private String alarmType;

    @ApiModelProperty("告警状态：PENDING-待处理、PROCESSING-处理中、RESOLVED-已解决、IGNORED-已忽略")
    private String alarmStatus;

    @ApiModelProperty("开始时间")
    private Date beginTime;

    @ApiModelProperty("结束时间")
    private Date endTime;




}
