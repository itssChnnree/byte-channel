package com.ruoyi.system.domain.dto;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 工单正文表(TicketMainText)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketMainTextDto {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("工单主表id")
    private String ticketId;

    @ApiModelProperty("提交用户id")
    private String userId;

    @ApiModelProperty("工单正文")
    private String ticketMainText;

    @ApiModelProperty("状态")
    private String status;

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
