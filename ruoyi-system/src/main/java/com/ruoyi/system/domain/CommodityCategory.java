package com.ruoyi.system.domain;

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
* 商品类别(CommodityCategory)ʵ����
*
* @author chenxiangyue
* @version v1.0.0
* @date 2025-07-20 23:23:46
*/
@TableName(value ="commodity_category" )
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class CommodityCategory extends ByteBaseEntity implements Serializable {
private static final long serialVersionUID = 1L;
    /**
        * 分类名
        */
    private String categoryName;
    /**
     * 分类描述
     */
    private String summary;
    /**
        * 可售zh
        */
    private Integer availableStatus;




}
