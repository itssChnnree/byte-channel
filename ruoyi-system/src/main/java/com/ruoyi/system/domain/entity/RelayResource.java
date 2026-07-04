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
 * 中转资源表(RelayResource)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10 14:57:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "relay_resource")
public class RelayResource extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 所属中转节点ID
     */
    @TableField(value = "node_id")
    private String nodeId;
    /**
     * 厂商账号ID
     */
    @TableField(value = "vendor_account_id")
    private String vendorAccountId;
    /**
     * vpsIp
     */
    @TableField(value = "resource_ip")
    private String resourceIp;
    /**
     * vps端口
     */
    @TableField(value = "resource_port")
    private String resourcePort;
    /**
     * 用户名
     */
    @TableField(value = "resource_user_name")
    private String resourceUserName;
    /**
     * 资源密码/密钥
     */
    @TableField(value = "resource_password")
    private String resourcePassword;
    /**
     * 可中转数量
     */
    @TableField(value = "transferable_quantity")
    private Integer transferableQuantity;
    /**
     * 是否可用
     */
    @TableField(value = "available_status")
    private Integer availableStatus;



}
