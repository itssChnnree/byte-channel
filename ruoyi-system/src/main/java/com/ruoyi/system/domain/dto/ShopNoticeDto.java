package com.ruoyi.system.domain.dto;

import java.util.Date;
import java.util.List;

import com.ruoyi.system.domain.base.PageBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 商城公告表(ShopNotice)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-23 22:53:07
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopNoticeDto extends PageBase {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("公告标题")
    private String noticeTitle;

    @ApiModelProperty("公告类型（1通知 2公告 3教程）")
    private String noticeType;

    @ApiModelProperty("公告内容")
    private String noticeContent;

    @ApiModelProperty("通知公告排序，由低到高排序，0展示最前面，相同按创建时间排序")
    private Integer sort;

    @ApiModelProperty("标签")
    private String tag;

    @ApiModelProperty("文档描述")
    private String documentDescription;

    @ApiModelProperty("状态,0为发布，1为草稿")
    private String status;

    @ApiModelProperty("附件列表")
    private List<ShopNoticeFileDto> fileUrls;


}
