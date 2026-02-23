package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;


/**
 * 流水线变更表(TransactionChangeLog)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-02-23 17:47:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "transaction_change_log")
public class TransactionChangeLog extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
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
     * 业务id
     */
    @TableField(value = "service_id")
    private String serviceId;
    /**
     * 业务类型
     */
    @TableField(value = "service_type")
    private String serviceType;
    /**
     * 业务值
     */
    @TableField(value = "service_value")
    private String serviceValue;

}
