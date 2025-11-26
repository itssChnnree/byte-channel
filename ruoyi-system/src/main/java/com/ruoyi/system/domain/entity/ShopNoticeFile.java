package com.ruoyi.system.domain.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;


import java.io.Serializable;


/**
 * 公告附件表(ShopNoticeFile)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-23 22:38:29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_notice_file")
@Builder
public class ShopNoticeFile extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 公告id
     */
    @TableField(value = "shop_notice_id")
    private String shopNoticeId;
    /**
     * 文件路径
     */
    @TableField(value = "file_url")
    private String fileUrl;
}
