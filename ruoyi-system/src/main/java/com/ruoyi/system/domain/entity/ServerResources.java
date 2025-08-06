package com.ruoyi.system.domain.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
* 服务器资源表(ServerResources)ʵ����
*
* @author chenxiangyue
* @version v1.0.0
* @date 2025-07-20 23:24:25
*/
@TableName(value ="server_resources" )
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ServerResources extends ByteBaseEntity implements Serializable {
private static final long serialVersionUID = 1L;

    /**
     * 服务器ip
     */
    @TableField(value = "resources_ip")
    private String resourcesIp;
    /**
     * 服务器端口
     */
    @TableField(value = "resources_port")
    private String resourcesPort;
    /**
     * 服务器密码
     */
    @TableField(value = "resources_password")
    private String resourcesPassword;
    /**
     * 服务器状态
     */
    @TableField(value = "resources_status")
    private String resourcesStatus;
    /**
     * 资源当前租户
     */
    @TableField(value = "resource_tenant")
    private String resourceTenant;
    /**
     * 租赁到期时间
     */
    @TableField(value = "lease_expiration_time")
    private Date leaseExpirationTime;
    /**
     * 售卖状态
     */
    @TableField(value = "sales_status")
    private String salesStatus;
    /**
     * 可售状态，可售状态,0为下架，1为上架
     */
    @TableField(value = "available_status")
    private Integer availableStatus;
    /**
     * 资源所属云服务商
     */
    @TableField(value = "cloud_service_provider")
    private String cloudServiceProvider;
    /**
     * 资源所属云服务商账号
     */
    @TableField(value = "cloud_user")
    private String cloudUser;
    /**
     * 节点导入链接
     */
    @TableField(value = "node_url")
    private String nodeUrl;
    /**
     * 节点端口
     */
    @TableField(value = "node_port")
    private String nodePort;

    /**
     * vless协议publickey
     */
    @TableField(value = "public_broker_key")
    private String publicBrokerKey;
    /**
     * vless协议publicKey
     */
    @TableField(value = "public_key")
    private String publicKey;
    /**
     * vless协议劫持域名
     */
    @TableField(value = "sni")
    private String sni;
    /**
     * vless协议短id
     */
    @TableField(value = "short_id")
    private String shortId;
    /**
     * vless协议用户id
     */
    @TableField(value = "user_id")
    private String userId;
    /**
     * 所属商品id
     */
    @TableField(value = "commodity_id")
    private String commodityId;


}
