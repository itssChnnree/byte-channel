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
 * 订单支付方式表(OrderPayType)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-23 21:16:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "order_pay_type")
public class OrderPayType extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 购买用户id
     */
    @TableField(value = "user_id")
    private String userId;
    /**
     * 订单id
     */
    @TableField(value = "order_id")
    private String orderId;
    /**
     * 微信订单id
     */
    @TableField(value = "wx_order_id")
    private String wxOrderId;
    /**
     * 支付宝订单id
     */
    @TableField(value = "alipay_order_id")
    private String alipayOrderId;
    /**
     * 是否进行最终核对，0为否，1为是
     */
    @TableField(value = "is_check")
    private Integer isCheck;
    /**
     * 是否进行最终核对，0为否，1为是
     */
    @TableField(value = "retry_count")
    private Integer retryCount;

}
