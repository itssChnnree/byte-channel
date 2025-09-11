package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 资源暂存表(ResourceAllocationTemporaryStorage)ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-11 15:28:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "resource_allocation_temporary_storage")
public class ResourceAllocationTemporaryStorage  implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 服务器ip
     */
    @TableField(value = "resources_ip")
    private String resourcesIp;
    /**
     * 节点端口
     */
    @TableField(value = "node_port")
    private Integer nodePort;
    /**
     * 公钥
     */
    @TableField(value = "public_key")
    private String publicKey;
    /**
     * 短id
     */
    @TableField(value = "short_id")
    private String shortId;
    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private String userId;
    /**
     * vless协议劫持域名
     */
    @TableField(value = "sni")
    private String sni;



}
