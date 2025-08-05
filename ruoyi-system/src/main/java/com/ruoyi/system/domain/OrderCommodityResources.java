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
* 订单商品资源(OrderCommodityResources)ʵ����
*
* @author chenxiangyue
* @version v1.0.0
* @date 2025-07-20 23:24:07
*/
@TableName(value ="order_commodity_resources" )
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrderCommodityResources extends ByteBaseEntity implements Serializable {
private static final long serialVersionUID = 1L;

/**
    * 购买用户id
    */
@TableField(value = "user_id")
private String userId;
/**
    * 订单商品id
    */
@TableField(value = "order_commodity_id")
private String orderCommodityId;
/**
    * 资源id
    */
@TableField(value = "resources_id")
private String resourcesId;
/**
    * 0为未删除，1为已删除
    */
@TableField(value = "is_deleted")
private Integer isDeleted;


}
