package com.ruoyi.system.domain.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.system.constant.EntityStatus;
import com.ruoyi.system.domain.base.PageBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;


/**
 * 资源屏蔽域名表(ResourceBlockDomain)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 14:37:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceBlockDomainDto extends PageBase {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("域名")
    @NotBlank(message = "域名不能为空")
    private String domain;

    @ApiModelProperty("前缀类型")
    @NotBlank(message = "前缀不能为空")
    private String prefixType;

    @ApiModelProperty("屏蔽理由")
    @NotBlank(message = "屏蔽理由不能为空")
    private String reason;

    @ApiModelProperty("状态")
    @NotBlank(message = "屏蔽理由不能为空")
    private String status;

    @ApiModelProperty("预约时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date scheduleTime;

    @ApiModelProperty("是否加入最近更新计划")
    private Boolean isAddToRecentUpdatePlan;

    public void setStatus(String status){
        if (EntityStatus.NORMAL.equals( status)){
            this.status = status;
        }else {
            this.status = EntityStatus.DISABLED;
        }
    }


}
