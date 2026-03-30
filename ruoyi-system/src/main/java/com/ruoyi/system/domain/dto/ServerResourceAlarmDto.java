package com.ruoyi.system.domain.dto;

import java.util.Date;
import java.util.List;

import com.ruoyi.system.constant.FaultHandingStatus;
import com.ruoyi.system.domain.base.PageBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 服务器资源故障告警表(ServerResourceAlarm)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-18 23:40:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerResourceAlarmDto extends PageBase {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("服务器资源id")
    private String resourceId;

    @ApiModelProperty("告警类型")
    private String alarmType;

    @ApiModelProperty("告警状态：PENDING-待处理、PROCESSING-处理中、RESOLVED-已解决、IGNORED-已忽略")
    private String alarmStatus;

    @ApiModelProperty("告警状态集合")
    private List<String> alarmStatusList;

    @ApiModelProperty("开始时间")
    private Date beginTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("商品分类ID")
    private String categoryId;

    @ApiModelProperty("商品ID")
    private String commodityId;

    @ApiModelProperty("资源IP")
    private String resourcesIp;

    public void setAlarmStatus(String alarmStatus) {
        if (FaultHandingStatus.FAULT.equals(alarmStatus)){
            this.alarmStatusList = List.of(FaultHandingStatus.PENDING, FaultHandingStatus.PROCESSING);
        }else {
            this.alarmStatus = alarmStatus;
        }
    }

}
