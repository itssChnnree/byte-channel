package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private Date createTime;

    public long getExpirationTimestamp() {
        if (createTime == null) {
            // 可能返回0或抛出异常？根据业务决定。这里假设createTime不为null。
            return 0L;
        }
        long millis = createTime.getTime() + 7L * 24 * 60 * 60 * 1000;
        return millis / 1000;
    }

}
