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
 * socks5资源记录(Socks5Resources)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-06-29 22:23:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "socks5_resources")
public class Socks5Resources extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
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
     * socks5端口
     */
    @TableField(value = "socks5_port")
    private String socks5Port;
    /**
     * socks5账号
     */
    @TableField(value = "socks5_user_name")
    private String socks5UserName;
    /**
     * socks5密码
     */
    @TableField(value = "socks5_password")
    private String socks5Password;
    /**
     * 查询密码
     */
    @TableField(value = "password")
    private String password;

}
