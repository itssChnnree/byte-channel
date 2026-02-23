package com.ruoyi.system.domain.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 流水线变更表(TransactionChangeLog)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-02-23 17:47:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionChangeLogVo {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("业务id")
    private String serviceId;

    @ApiModelProperty("业务类型")
    private String serviceType;

    @ApiModelProperty("业务值")
    private String serviceValue;

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


}
