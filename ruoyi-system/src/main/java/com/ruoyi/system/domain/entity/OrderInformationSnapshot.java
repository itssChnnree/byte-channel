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
 * 订单快照(OrderInformationSnapshot)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-22 15:11:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "order_information_snapshot")
public class OrderInformationSnapshot extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 订单id
     */
    @TableField(value = "order_id")
    private String orderId;
    /**
     * 购买用户id
     */
    @TableField(value = "user_id")
    private String userId;
    /**
     * 商品id
     */
    @TableField(value = "commodity_id")
    private String commodityId;
    /**
     * 商品名
     */
    @TableField(value = "commodity_name")
    private String commodityName;
    /**
     * 商品概述
     */
    @TableField(value = "commodity_desc")
    private String commodityDesc;
    /**
     * 带宽
     */
    @TableField(value = "bandwidth")
    private Integer bandwidth;
    /**
     * 适合业务
     */
    @TableField(value = "business_suitable")
    private String businessSuitable;
    /**
     * 商品分类id
     */
    @TableField(value = "category_id")
    private String categoryId;
    /**
     * 分类名
     */
    @TableField(value = "category_name")
    private String categoryName;
    /**
     * 商品分类概述
     */
    @TableField(value = "category_desc")
    private String categoryDesc;



}
