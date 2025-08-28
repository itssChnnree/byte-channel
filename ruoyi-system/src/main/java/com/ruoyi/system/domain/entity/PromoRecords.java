package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


/**
* 推广记录表(PromoRecords)ʵ����
*
* @author chenxiangyue
* @version v1.0.0
* @date 2025-07-20 23:24:19
*/
@TableName(value ="promo_records" )
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PromoRecords  extends ByteBaseEntity implements Serializable {
private static final long serialVersionUID = 1L;

    /**
        * 推荐人用户id
        */
    @TableField(value = "user_id")
    private String userId;
    /**
        * 被推荐人用户id
        */
    @TableField(value = "referrals_user_id")
    private String referralsUserId;
    /**
        * 推广码记录id
        */
    @TableField(value = "promo_code_records_id")
    private String promoCodeRecordsId;
    /**
        * 订单id
        */
    @TableField(value = "order_id")
    private String orderId;
    /**
        * 返现金额
        */
    @TableField(value = "cashback_amount")
    private BigDecimal cashbackAmount;

    /**
        * 被推荐人购买金额
        */
    @TableField(value = "Purchase_amount")
    private BigDecimal purchaseAmount;
    
    /**
        * 返现比例
        */
    @TableField(value = "cashback_percentage")
    private String cashbackPercentage;



}
