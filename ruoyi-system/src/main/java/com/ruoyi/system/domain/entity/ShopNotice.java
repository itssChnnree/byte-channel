package com.ruoyi.system.domain.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


import java.io.Serializable;


/**
 * 商城公告表(ShopNotice)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-23 22:53:07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_notice")
public class ShopNotice extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 公告标题
     */
    @TableField(value = "notice_title")
    private String noticeTitle;
    /**
     * 公告类型（1通知 2公告）
     */
    @TableField(value = "notice_type")
    private String noticeType;
    /**
     * 公告内容
     */
    @TableField(value = "notice_content")
    private String noticeContent;
    /**
     * 通知公告排序，由低到高排序，0展示最前面，相同按创建时间排序
     */
    @TableField(value = "sort")
    private Integer sort;

    @TableField(value = "tag")
    private String tag;

    @ApiModelProperty("document_description")
    private String documentDescription;

    /**
     * 状态,0为发布，1为草稿
     */
    @TableField(value = "status")
    private String status;

}
