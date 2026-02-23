package com.ruoyi.system.domain.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;


/**
 * 工单正文订单信息表(TicketMainTextOrder)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-02-23 17:52:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "ticket_main_text_order")
public class TicketMainTextOrder extends ByteBaseEntity implements Serializable {
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
     * 订单id
     */
    @TableField(value = "order_id")
    private String orderId;



}
