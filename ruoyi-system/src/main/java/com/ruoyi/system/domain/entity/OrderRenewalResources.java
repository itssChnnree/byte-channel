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
/**
    * 0为未删除，1为已删除
    */
@TableField(value = "is_deleted")
private Integer isDeleted;
/**
    * 创建人
    */
@TableField(value = "create_user")
private String createUser;
/**
    * 修改人
    */
@TableField(value = "update_user")
private String updateUser;
/**
    * 创建时间
    */
@TableField(value = "create_time")
private Date createTime;
/**
    * 修改时间
    */
@TableField(value = "update_time")
private Date updateTime;



}
