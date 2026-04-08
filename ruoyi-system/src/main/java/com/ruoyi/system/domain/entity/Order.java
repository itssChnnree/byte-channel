package com.ruoyi.system.domain.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


/**
* 订单表(Order)ʵ����
*
* @author chenxiangyue
* @version v1.0.0
* @date 2025-07-20 23:24:02
*/
@TableName(value ="order" )
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Order  extends ByteBaseEntity implements Serializable {
private static final long serialVersionUID = 1L;

        /**
            * 购买用户id
            */
        @TableField(value = "user_id")
        private String userId;
        /**
            * 订单金额
            */
        @TableField(value = "amount")
        private BigDecimal amount;
        /**
         * 交易描述
         */
        @TableField(value = "description")
        private String description;

        /**
         * 第三方支付id
         */
        @TableField(value = "payment_id")
        private String paymentId;

        /**
         * 支付方式
         */
        @TableField(value = "payment_type")
        private String paymentType;

        /**
         * 订单类型,参数值见 OrderTypeConstant
         */
        @TableField(value = "order_type")
        private String orderType;


        /**
         * 付款周期,参数值详见 PaymentPeriodConstant
         */
        @TableField(value = "payment_period")
        private String paymentPeriod;
        /**
         * 是否退款到余额，0为否，1为是，用作用户退款时
         */
        @TableField(value = "refound_to_balance")
        private Integer refoundToBalance;

        /**
            * 订单状态
            */
        @TableField(value = "status")
        private String status;
        /**
            * 下单时间
            */
        @TableField(value = "order_time")
        private Date orderTime;
        /**
            * 0为未删除，1为已删除
            */
        @TableField(value = "is_deleted")
        private Integer isDeleted;



}
