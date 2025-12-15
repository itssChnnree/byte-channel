package com.ruoyi.system.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 域名屏蔽重启节点预约时间(ScheduledDomainLockingTime)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 14:13:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledDomainLockingTimeVo {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("预约重启时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date scheduledTime;

    @ApiModelProperty("0为未删除，1为已删除")
    private Integer isDeleted;

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
