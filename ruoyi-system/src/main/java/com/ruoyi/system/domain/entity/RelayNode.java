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
 * 中转节点表(RelayNode)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10 14:57:36
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "relay_node")
public class RelayNode extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 节点名称
     */
    @TableField(value = "node_name")
    private String nodeName;
    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;
    /**
     * 可用域名
     */
    @TableField(value = "server_names")
    private String serverNames;
    /**
     * 劫持域名
     */
    @TableField(value = "dest")
    private String dest;

    /**
     * 启用状态 0停用 1启用
     */
    @TableField(value = "available_status")
    private Integer availableStatus;
}
