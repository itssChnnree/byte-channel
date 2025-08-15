package com.ruoyi.system.domain.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
* 订单商品(OrderCommodity)ʵ����
*
* @author chenxiangyue
* @version v1.0.0
* @date 2025-08-15 10:01:36
*/
@EqualsAndHashCode(callSuper = true)
@TableName(value ="order_commodity" )
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCommodity extends ByteBaseEntity implements Serializable {
private static final long serialVersionUID = 1L;
/**
    * 主键id
    */
@TableId@TableField(value = "id")
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
    * 购买数量
    */
@TableField(value = "purchase_quantity")
private Integer purchaseQuantity;
/**
    * 交付数量
    */
@TableField(value = "order_quantity")
private Integer orderQuantity;
/**
    * 商品id
    */
@TableField(value = "commodity_id")
private String commodityId;


}
