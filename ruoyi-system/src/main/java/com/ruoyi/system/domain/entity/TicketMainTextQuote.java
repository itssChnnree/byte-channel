package com.ruoyi.system.domain.entity;

import java.math.BigDecimal;
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
 * 工单正文报价表(TicketMainTextQuote)ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-10-14 22:36:23
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "ticket_main_text_quote")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketMainTextQuote extends ByteBaseEntity implements Serializable {
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
     * 报价
     */
    @TableField(value = "quote")
    private BigDecimal quote;

    /**
     * 报价订单id
     */
    @TableField(value = "quote_order_id")
    private String quoteOrderId;

}
