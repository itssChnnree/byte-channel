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
 * 工单正文表(TicketMainText)ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:36
 */
@TableName(value = "ticket_main_text")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class TicketMainText extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 工单主表id
     */
    @TableField(value = "ticket_id")
    private String ticketId;
    /**
     * 提交用户id
     */
    @TableField(value = "user_id")
    private String userId;
    /**
     * 工单正文
     */
    @TableField(value = "ticket_main_text")
    private String ticketMainText;

    /**
     * 工单回复人用户名
     */
    @TableField(value = "user_name")
    private String userName;


}
