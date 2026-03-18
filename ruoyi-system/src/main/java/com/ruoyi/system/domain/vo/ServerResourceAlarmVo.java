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

    @ApiModelProperty("0为未删除，1为已删除")
    private Integer isDeleted;

    @ApiModelProperty("开始时间")
    private Date beginTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("创建人")
    private String createUser;

    @ApiModelProperty("修改人")
    private String updateUser;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("状态（暂时弃用）")
    private String status;


}
