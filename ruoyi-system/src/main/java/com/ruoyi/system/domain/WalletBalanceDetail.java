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
* 钱包余额明细表(WalletBalanceDetail)ʵ����
*
* @author chenxiangyue
* @version v1.0.0
* @date 2025-07-20 23:24:37
*/
@TableName(value ="wallet_balance_detail" )
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class WalletBalanceDetail extends ByteBaseEntity implements Serializable {
private static final long serialVersionUID = 1L;

/**
    * 用户id
    */
@TableField(value = "user_id")
private String userId;
/**
    * 变更金额
    */
@TableField(value = "change_amount")
private Double changeAmount;
/**
    * 变更类型
    */
@TableField(value = "type")
private String type;



}
