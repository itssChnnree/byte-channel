package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * [利润折线图]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfitFlowLineChartVo {

    @ApiModelProperty(value = "时间")
    private String date;

    @ApiModelProperty(value = "利润")
    private BigDecimal profit;

    public BigDecimal getProfit() {
        return profit != null ? profit.setScale(2, BigDecimal.ROUND_HALF_UP) : null;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }
}
