package com.ruoyi.system.domain.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 工单正文订单信息表(TicketMainTextOrder)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-02-23 17:52:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketMainTextOrderDto {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("工单正文表id")
    private String ticketMainTextId;

    @ApiModelProperty("订单id")
    private String orderId;

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
