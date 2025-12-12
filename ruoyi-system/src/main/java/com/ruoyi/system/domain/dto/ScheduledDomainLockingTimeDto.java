package com.ruoyi.system.domain.dto;

import java.util.Date;

import com.ruoyi.system.domain.base.PageBase;
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
    private String id;

    @ApiModelProperty("预约重启时间")
    @NotNull(message = "预约重启时间不能为空")
    private Date scheduledTime;

    @ApiModelProperty("状态")
    private String status;


}
