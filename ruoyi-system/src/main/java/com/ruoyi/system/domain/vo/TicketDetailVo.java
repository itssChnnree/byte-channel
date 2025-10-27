package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


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
public class TicketDetailVo {

    @ApiModelProperty("工单编号")
    private String id;

    @ApiModelProperty("提交用户id")
    private String userId;

    @ApiModelProperty("提交人")
    private String submitUser;

    @ApiModelProperty("工单类型")
    private String ticketType;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("工单状态")
    private String status;

    @ApiModelProperty("工单描述")
    private String description;

    @ApiModelProperty("工单标题")
    private String ticketTitle;

    @ApiModelProperty("工单正文集合")
    private List<TicketMainTextDetailVo> ticketMainTextDetailVos;


}
