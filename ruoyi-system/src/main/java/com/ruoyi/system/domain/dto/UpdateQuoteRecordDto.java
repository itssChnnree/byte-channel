package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改报价处理记录DTO
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-04-06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateQuoteRecordDto {

    @ApiModelProperty("订单ID")
    private String orderId;

    @ApiModelProperty("报价处理记录（HTML富文本）")
    private String quoteProcessingRecord;
}
