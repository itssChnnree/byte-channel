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
* 服务器资源表(ServerResourcesRenewal)ʵ����
*
* @author chenxiangyue
* @version v1.0.0
* @date 2025-10-28 10:48:26
*/

@TableName(value ="server_resources_renewal" )
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ServerResourcesRenewal extends ByteBaseEntity implements Serializable {
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
        * 续费开关,0为关，1为开
        */
    @TableField(value = "renewal_switch")
    private Integer renewalSwitch;
    /**
        * 开启用户
        */
    @TableField(value = "user_id")
    private String userId;
    /**
        * 续费周期（月）
        */
    @TableField(value = "renewal_period")
    private Integer renewalPeriod;
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
    /**
        * 状态（暂时弃用）
        */
    @TableField(value = "status")
    private String status;

}
