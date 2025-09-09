package com.ruoyi.system.domain.vo;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 厂商信息表(VendorInformation)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-08 16:47:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorInformationVo {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("厂商名")
    private String vendorName;

    @ApiModelProperty("厂商网址")
    private String vendorUrl;

    @ApiModelProperty("厂商描述")
    private String vendorDescription;

    @ApiModelProperty("账号数量")
    private Integer accountCount;

}
