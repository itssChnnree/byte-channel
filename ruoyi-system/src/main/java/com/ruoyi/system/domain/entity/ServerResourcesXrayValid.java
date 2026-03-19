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
* 资源状态检查节点分布(ServerResourcesXrayValid)ʵ����
*
* @author chenxiangyue
* @version v1.0.0
* @date 2025-08-05 17:32:30
*/
@TableName(value ="server_resources_xray_valid" )
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ServerResourcesXrayValid extends ByteBaseEntity implements Serializable {
private static final long serialVersionUID = 1L;
    /**
        * 主键id
        */
    @TableId@TableField(value = "id")
    private String id;
    /**
        * 资源id
        */
    @TableField(value = "resources_id")
    private String resourcesId;
    /**
        * 校验服务器ip
        */
    @TableField(value = "web_ip")
    private String webIpPort;

    /**
        * 校验服务器-xray代理端口
        */
    @TableField(value = "xray_port")
    private String xrayPort;

}
