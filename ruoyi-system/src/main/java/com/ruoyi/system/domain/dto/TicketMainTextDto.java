package com.ruoyi.system.domain.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


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
    @NotBlank(message = "工单主表id不能为空")
    private String ticketId;

    @ApiModelProperty("报价")
    private BigDecimal quote;

    @ApiModelProperty("提交用户id")
    private String userId;

    @ApiModelProperty("工单正文")
    @NotBlank(message = "请填写正文")
    private String ticketMainText;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty(value = "工单回复人用户名")
    private String userName;

    @ApiModelProperty("工单正文文件路径")
    private List<String> fileUrlList;
}
