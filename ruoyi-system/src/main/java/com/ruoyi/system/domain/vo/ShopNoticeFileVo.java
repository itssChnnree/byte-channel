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

    @ApiModelProperty("文件名")
    private String fileName;
}
