package com.ruoyi.system.domain.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


import java.io.Serializable;


/**
 * 订单状态时间线记录表（一行记录所有变更时间）(OrderStatusTimeline)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-19 21:20:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "order_status_timeline")
public class OrderStatusTimeline implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 订单ID（唯一）
     */
    @TableField(value = "order_id")
    private String orderId;
    /**
     * 1.创建订单时间
     */
    @TableField(value = "order_created_time")
    private Date orderCreatedTime;
    /**
     * 2.待支付时间
     */
    @TableField(value = "wait_pay_time")
    private Date waitPayTime;
    /**
     * 3.超时系统自动取消时间
     */
    @TableField(value = "timeout_canceled_time")
    private Date timeoutCanceledTime;
    /**
     * 4.用户支付时间
     */
    @TableField(value = "user_pay_time")
    private Date userPayTime;
    /**
     * 5.待分配资源时间
     */
    @TableField(value = "wait_allocation_time")
    private Date waitAllocationTime;
    /**
     * 6.已完成时间
     */
    @TableField(value = "completed_time")
    private Date completedTime;
    /**
     * 7.用户取消时间
     */
    @TableField(value = "user_canceled_time")
    private Date userCanceledTime;
    /**
     * 8.待退款时间
     */
    @TableField(value = "wait_refund_time")
    private Date waitRefundTime;
    /**
     * 9.已退款时间
     */
    @TableField(value = "refund_success_time")
    private Date refundSuccessTime;


}
