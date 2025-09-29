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
 * 工单主表(Ticket)ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:35
 */
@TableName(value = "ticket")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Ticket extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 提交用户id
     */
    @TableField(value = "user_id")
    private String userId;
    /**
     * 工单类型
     */
    @TableField(value = "ticket_type")
    private String ticketType;
    /**
     * 工单状态
     */
    @TableField(value = "status")
    private String status;
    /**
     * 工单描述
     */
    @TableField(value = "description")
    private String description;
    /**
     * 工单标题
     */
    @TableField(value = "ticket_title")
    private String ticketTitle;
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
