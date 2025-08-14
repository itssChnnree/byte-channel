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
 * 用户购物车(ShoppingCart)ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-06 23:43:08
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "shopping_cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCart extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private String userId;
    /**
     * 商品id
     */
    @TableField(value = "commodity_id")
    private String commodityId;
    /**
     * 商品数量
     */
    @TableField(value = "commodity_num")
    private Integer commodityNum;

}
