package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [资源导入数据]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/9/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourcesImportVo {

    private String resourcesId;

    @ApiModelProperty("vless链接")
    private String vlessUrl;

    @ApiModelProperty("clash下载链接")
    private String clashDownloadUrl;
}
