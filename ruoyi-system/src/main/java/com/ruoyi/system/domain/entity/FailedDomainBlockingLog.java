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
 * 域名屏蔽未成功记录表(FailedDomainBlockingLog)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 21:47:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "failed_domain_blocking_log")
public class FailedDomainBlockingLog extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 资源id
     */
    @TableField(value = "server_resources_id")
    private String serverResourcesId;
    /**
     * 失败理由
     */
    @TableField(value = "fail_reason")
    private String failReason;
}
