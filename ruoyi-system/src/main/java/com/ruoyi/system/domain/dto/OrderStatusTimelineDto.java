package com.ruoyi.system.domain.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 订单状态时间线记录表（一行记录所有变更时间）(OrderStatusTimeline)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-19 21:20:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusTimelineDto {

    @ApiModelProperty("主键ID")
    private String id;

    @ApiModelProperty("订单ID（唯一）")
    private String orderId;

    @ApiModelProperty("1.创建订单时间")
    private Date orderCreatedTime;

    @ApiModelProperty("2.待支付时间")
    private Date waitPayTime;

    @ApiModelProperty("3.超时系统自动取消时间")
    private Date timeoutCanceledTime;

    @ApiModelProperty("4用户支付时间")
    private Date userPayTime;

    @ApiModelProperty("5.待分配资源时间")
    private Date waitAllocationTime;

    @ApiModelProperty("6.已完成时间")
    private Date completedTime;

    @ApiModelProperty("7.用户取消时间")
    private Date userCanceledTime;

    @ApiModelProperty("8.待退款时间")
    private Date waitRefundTime;

    @ApiModelProperty("9.已退款时间")
    private Date refundSuccessTime;


}
