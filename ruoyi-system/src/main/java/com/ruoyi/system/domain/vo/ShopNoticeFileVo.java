package com.ruoyi.system.domain.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 公告附件表(ShopNoticeFile)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-23 22:38:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopNoticeFileVo {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("工单正文表id")
    private String shopNoticeId;

    @ApiModelProperty("文件路径")
    private String fileUrl;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("0为未删除，1为已删除")
    private Integer isDeleted;

    @ApiModelProperty("创建人")
    private String createUser;

    @ApiModelProperty("修改人")
    private String updateUser;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;


}
