package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 工单正文订单信息表(TicketMainTextOrder)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-02-23 17:52:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketMainTextOrderVo {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("工单正文表id")
    private String ticketMainTextId;

    @ApiModelProperty("订单id")
    private String orderId;

    @ApiModelProperty("购买用户id")
    private String userId;

    @ApiModelProperty("订单金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "交易描述")
    private String description;

    @ApiModelProperty("订单状态")
    private String status;

    @ApiModelProperty("下单时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date orderTime;

    @ApiModelProperty("订单类型")
    private String orderType;

    @ApiModelProperty("付款周期")
    private String paymentPeriod;

    @ApiModelProperty(value = "第三方支付id")
    private String paymentId;

    @ApiModelProperty(value = "支付方式")
    private String paymentType;

    private Integer isDeleted;


}
