package com.ruoyi.system.domain.dto;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 工单主表(Ticket)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketDto {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("提交用户id")
    private String userId;

    @ApiModelProperty("工单类型")
    private String ticketType;

    @ApiModelProperty("工单状态")
    private String status;

    @ApiModelProperty("工单描述")
    private String description;

    @ApiModelProperty("工单标题")
    private String ticketTitle;

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
