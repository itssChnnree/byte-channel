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
 * 服务器资源故障告警表(ServerResourceAlarm)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-18 23:40:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "server_resource_alarm")
public class ServerResourceAlarm extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 服务器资源id
     */
    @TableField(value = "resource_id")
    private String resourceId;
    /**
     * 告警类型
     */
    @TableField(value = "alarm_type")
    private String alarmType;
    /**
     * 告警状态：PENDING-待处理、PROCESSING-处理中、RESOLVED-已解决、IGNORED-已忽略
     */
    @TableField(value = "alarm_status")
    private String alarmStatus;
    /**
     * 0为未删除，1为已删除
     */
    @TableField(value = "is_deleted")
    private Integer isDeleted;
    /**
     * 开始时间
     */
    @TableField(value = "begin_time")
    private Date beginTime;
    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    private Date endTime;
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
