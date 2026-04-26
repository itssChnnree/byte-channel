package com.ruoyi.system.domain.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 订单报价表(OrderQuote)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-02-27 00:11:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderQuoteVo {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("购买用户id")
    private String userId;

    @ApiModelProperty("订单id")
    private String orderId;

    @ApiModelProperty("报价处理记录")
    private String quoteProcessingRecord;

}
