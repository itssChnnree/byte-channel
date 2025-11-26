package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * [订单详情]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/11/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailVo{

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("购买用户id")
    private String userId;

    @ApiModelProperty("订单金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "交易描述")
    private String description;

    @ApiModelProperty("订单状态")
    private String status;

    @ApiModelProperty("下单时间")
    private Date orderTime;

    @ApiModelProperty("订单类型")
    private String orderType;

    @ApiModelProperty("付款周期")
    private String paymentPeriod;

    @ApiModelProperty(value = "支付方式")
    private String paymentType;

    @ApiModelProperty("时间线")
    private OrderStatusTimelineVo orderStatusTimelineVo;
}
