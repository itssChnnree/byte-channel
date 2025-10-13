package com.ruoyi.system.domain.dto;

import java.util.Date;
import java.util.List;

import com.ruoyi.system.domain.base.PageBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


/**
 * 工单主表(Ticket)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:34
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketDto extends PageBase {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("提交用户id")
    private String userId;

    @ApiModelProperty("工单类型")
    @NotBlank(message = "工单类型不能为空")
    private String ticketType;

    @ApiModelProperty("工单状态")
    private String status;

    @ApiModelProperty("工单描述")
    @NotBlank(message = "工单描述不能为空")
    private String description;

    @ApiModelProperty("工单标题")
    @NotBlank(message = "工单标题不能为空")
    private String ticketTitle;


    @ApiModelProperty("工单正文")
    @NotBlank(message = "工单正文不能为空")
    private String ticketMainText;

    @ApiModelProperty("工单正文文件路径")
    private List<String> fileUrlList;


}
