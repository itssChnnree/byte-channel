package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


/**
 * 工单正文详情表(TicketMainText)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketMainTextDetailVo {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("工单主表id")
    private String ticketId;

    @ApiModelProperty("提交用户id")
    private String userId;

    @ApiModelProperty("工单正文")
    private String ticketMainText;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty(value = "工单回复人用户名")
    private String userName;

    @ApiModelProperty(value = "是否我方")
    private Boolean isMe;

    @ApiModelProperty(value = "工单报价")
    private TicketMainTextQuoteVo ticketMainTextQuoteVo;

    @ApiModelProperty(value = "工单订单")
    private TicketMainTextOrderVo ticketMainTextOrderVo;

    @ApiModelProperty(value = "工单正文文件集合 ")
    private List<TicketMainTextFileVo> ticketMainTextFileVos;
}
