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
 * 工单正文文件附件表(TicketMainTextFile)ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:37
 */
@TableName(value = "ticket_main_text_file")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class TicketMainTextFile extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 工单正文表id
     */
    @TableField(value = "ticket_main_text_id")
    private String ticketMainTextId;
    /**
     * 文件路径
     */
    @TableField(value = "file_url")
    private String fileUrl;
}
