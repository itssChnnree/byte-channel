package com.ruoyi.system.domain.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;

import java.io.Serializable;


/**
* 订单-续费资源表(OrderRenewalResources)ʵ����
*
* @author chenxiangyue
* @version v1.0.0
* @date 2025-11-15 23:43:58
*/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="order_renewal_resources" )
@Builder
public class OrderRenewalResources extends ByteBaseEntity implements Serializable {
private static final long serialVersionUID = 1L;
/**
    * 主键id
    */
@TableId@TableField(value = "id")
private String id;
/**
    * 订单id
    */
@TableField(value = "order_id")
private String orderId;
/**
    * 资源ip
    */
@TableField(value = "resources_ip")
private String resourcesIp;
/**
    * 资源id
    */
@TableField(value = "resources_id")
private String resourcesId;
/**
    * 购买用户id
    */
@TableField(value = "user_id")
private String userId;
}
