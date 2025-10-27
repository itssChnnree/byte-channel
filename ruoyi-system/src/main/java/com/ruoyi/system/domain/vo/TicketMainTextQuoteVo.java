package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 工单正文报价表(TicketMainTextQuote)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-10-14 22:36:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketMainTextQuoteVo {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("工单正文表id")
    private String ticketMainTextId;

    @ApiModelProperty("报价")
    private BigDecimal quote;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("报价订单id")
    private String quoteOrderId;
}
