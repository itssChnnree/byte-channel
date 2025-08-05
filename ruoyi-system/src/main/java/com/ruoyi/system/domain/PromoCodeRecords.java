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
* 推广码记录表(PromoCodeRecords)ʵ����
*
* @author chenxiangyue
* @version v1.0.0
* @date 2025-07-20 23:24:13
*/
@TableName(value ="promo_code_records" )
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PromoCodeRecords extends ByteBaseEntity implements Serializable {
private static final long serialVersionUID = 1L;

/**
    * 用户id
    */
@TableField(value = "user_id")
private String userId;
/**
    * 推广码
    */
@TableField(value = "promo_code")
private String promoCode;
/**
    * 状态
    */
@TableField(value = "status")
private String status;

}
