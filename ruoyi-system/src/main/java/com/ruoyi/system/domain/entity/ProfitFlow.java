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
 * 利润流水表(ProfitFlow)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-15 00:12:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "profit_flow")
public class ProfitFlow extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 利润金额（正数收入，负数支出）
     */
    @TableField(value = "profit_amount")
    private BigDecimal profitAmount;
    /**
     * 来源类型
     */
    @TableField(value = "source_type")
    private String sourceType;
    /**
     * 来源业务ID（如订单ID）
     */
    @TableField(value = "source_id")
    private String sourceId;
    /**
     * 来源描述
     */
    @TableField(value = "source_desc")
    private String sourceDesc;
    /**
     * 图片URL
     */
    @TableField(value = "image_url")
    private String imageUrl;
    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;


}
