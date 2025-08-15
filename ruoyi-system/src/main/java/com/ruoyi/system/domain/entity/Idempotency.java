package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
* 幂等性控制表(Idempotency)ʵ����
*
* @author chenxiangyue
* @version v1.0.0
* @date 2025-08-15 14:25:31
*/
@EqualsAndHashCode(callSuper = true)
@TableName(value ="idempotency" )
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Idempotency extends ByteBaseEntity implements Serializable {
private static final long serialVersionUID = 1L;
/**
    * 主键id
    */
@TableId@TableField(value = "id")
private String id;
/**
    * 请求唯一标识（业务方生成）
    */
@TableField(value = "request_id")
private String requestId;
/**
    * 业务类型（如：payment/order）
    */
@TableField(value = "business_type")
private String businessType;


}
