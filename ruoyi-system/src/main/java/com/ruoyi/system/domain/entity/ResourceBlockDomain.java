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
 * 资源屏蔽域名表(ResourceBlockDomain)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 14:37:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "resource_block_domain")
public class ResourceBlockDomain extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 域名
     */
    @TableField(value = "domain")
    private String domain;
    /**
     * 前缀类型
     */
    @TableField(value = "prefix_type")
    private String prefixType;
    /**
     * 屏蔽理由
     */
    @TableField(value = "reason")
    private String reason;
}
