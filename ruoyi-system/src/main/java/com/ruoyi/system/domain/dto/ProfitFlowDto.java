package com.ruoyi.system.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.system.domain.base.PageBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 利润流水表(ProfitFlow)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-15 00:12:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProfitFlowDto extends PageBase {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("利润金额（正数收入，负数支出）")
    private BigDecimal profitAmount;

    @ApiModelProperty("来源类型")
    private String sourceType;

    @ApiModelProperty("来源业务ID（如订单ID）")
    private String sourceId;

    @ApiModelProperty("来源描述")
    private String sourceDesc;

    @ApiModelProperty("图片URL")
    private String imageUrl;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;


}
