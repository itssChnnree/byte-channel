package com.ruoyi.system.domain.vo;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 商城公告表(ShopNotice)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-23 22:53:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopNoticeVo {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("公告标题")
    private String noticeTitle;

    @ApiModelProperty("公告类型（1通知 2公告）")
    private String noticeType;

    @ApiModelProperty("公告内容")
    private String noticeContent;

    @ApiModelProperty("通知公告排序，由低到高排序，0展示最前面，相同按创建时间排序")
    private Integer sort;

    @ApiModelProperty("状态,0为发布，1为草稿")
    private String status;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("标签")
    private String tag;

    @ApiModelProperty("文档描述")
    private String documentDescription;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("修改人")
    private String updateUser;

    @ApiModelProperty("创建人")
    private String createUser;

    @ApiModelProperty("附件列表")
    private List<ShopNoticeFileVo> shopNoticeFiles;



}
