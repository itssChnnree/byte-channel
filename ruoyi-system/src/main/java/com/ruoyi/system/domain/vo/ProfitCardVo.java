package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * [入参]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfitCardVo {

    // 日利润
    @ApiModelProperty(value = "日利润")
    private BigDecimal dayProfit;
    // 月利润
    @ApiModelProperty(value = "月利润")
    private BigDecimal monthProfit;
    // 年利润
    @ApiModelProperty(value = "年利润")
    private BigDecimal yearProfit;

}
