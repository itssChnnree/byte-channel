package com.ruoyi.system.domain.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.system.domain.base.PageBase;
import com.ruoyi.system.group.InsertGroup;
import com.ruoyi.system.group.UpdateGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;


/**
 * 域名屏蔽重启节点预约时间(ScheduledDomainLockingTime)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 14:13:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledDomainLockingTimeDto extends PageBase {

    @ApiModelProperty("主键id")
    @NotNull(message = "请选择需要修改的预约时间", groups = {UpdateGroup.class})
    private String id;

    @ApiModelProperty("预约重启时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @NotNull(message = "预约重启时间不能为空", groups = {UpdateGroup.class, InsertGroup.class})
    private Date scheduledTime;

    @ApiModelProperty("状态")
    private String status;


}
