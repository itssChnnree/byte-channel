package com.ruoyi.system.domain;

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
private Double amount;
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
