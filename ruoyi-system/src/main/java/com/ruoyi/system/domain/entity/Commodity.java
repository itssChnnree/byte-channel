package com.ruoyi.system.domain.entity;



import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


/**
* 商品表(Commodity)ʵ����
*
* @author chenxiangyue
* @version v1.0.0
* @date 2025-07-20 23:23:33
*/
@EqualsAndHashCode(callSuper = true)
@TableName(value ="commodity" )
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Commodity extends ByteBaseEntity implements Serializable {
private static final long serialVersionUID = 1L;

    /**
        * 商品名
        */
    @TableField(value = "commodity_name")
    private String commodityName;
    /**
        * 价格
        */
    @TableField(value = "commodity_price")
    private BigDecimal commodityPrice;
    /**
        * 折后价
        */
    @TableField(value = "commodity_discounted_price")
    private BigDecimal commodityDiscountedPrice;
    /**
        * 商品类别id
        */
    @TableField(value = "category_id")
    private String categoryId;
    /**
        * 超卖配置数
        */
    @TableField(value = "oversold_configurations")
    private Integer oversoldConfigurations;
    /**
        * 已超卖数
        */
    @TableField(value = "oversold")
    private Integer oversold;
    /**
        * 库存
        */
    @TableField(value = "inventory")
    private Integer inventory;
    /**
        * 告警阈值
        */
    @TableField(value = "alarm_threshold")
    private Integer alarmThreshold;
    /**
        * 描述
        */
    @TableField(value = "description")
    private String description;
    /**
        * 类别
        */
    @TableField(value = "category")
    private String category;
    /**
        * 可售状态,0为下架，1为上架
        */
    @TableField(value = "available_status")
    private Integer availableStatus;

    /**
     * 适合业务
     */
    @TableField(value = "business_suitable")
    private String businessSuitable;

    /**
     * 带宽
     */
    @TableField(value = "bandwidth")
    private String bandwidth;

    @TableField("dest")
    private String dest;

    @TableField("server_names")
    private String serverNames;

}
