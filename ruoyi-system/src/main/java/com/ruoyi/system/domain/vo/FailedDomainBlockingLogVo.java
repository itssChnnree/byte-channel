package com.ruoyi.system.domain.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 域名屏蔽未成功记录表(FailedDomainBlockingLog)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 21:47:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FailedDomainBlockingLogVo {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("资源id")
    private String serverResourcesId;

    @ApiModelProperty("失败理由")
    private String failReason;

    @ApiModelProperty("资源ip")
    private String ip;

    @ApiModelProperty("商品名称")
    private String commodityName;

    @ApiModelProperty("商品分类名称")
    private String categoryName;

}
