package com.ruoyi.system.domain.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


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
public class ResourceBlockDomainVo {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("域名")
    private String domain;

    @ApiModelProperty("前缀类型")
    private String prefixType;

    @ApiModelProperty("屏蔽理由")
    private String reason;

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
