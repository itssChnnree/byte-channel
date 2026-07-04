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
 * 中转端口表(RelayPort)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10 14:57:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "relay_port")
public class RelayPort extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 所属中转资源ID
     */
    @TableField(value = "relay_resource_id")
    private String relayResourceId;
    /**
     * 目标资源ID（转发目标）
     */
    @TableField(value = "target_resource_id")
    private String targetResourceId;
    /**
     * 转发端口
     */
    @TableField(value = "port")
    private String port;
    /**
     * 协议类型（vmess/vless/trojan/ss）
     */
    @TableField(value = "protocol")
    private String protocol;
    /**
     * 传输方式（ws/tcp/grpc/h2）
     */
    @TableField(value = "transport")
    private String transport;
    /**
     * 传输路径
     */
    @TableField(value = "path")
    private String path;
    /**
     * 加密方式（none/tls/xtls）
     */
    @TableField(value = "security")
    private String security;
    /**
     * 公钥
     */
    @TableField(value = "public_key")
    private String publicKey;
    /**
     * SNI域名
     */
    @TableField(value = "sni")
    private String sni;
    /**
     * 短ID
     */
    @TableField(value = "short_id")
    private String shortId;
    /**
     * 节点端口
     */
    @TableField(value = "node_port")
    private String nodePort;

}
