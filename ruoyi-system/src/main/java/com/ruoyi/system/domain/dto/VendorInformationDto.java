package com.ruoyi.system.domain.dto;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


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
public class VendorInformationDto {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("厂商名")
    @NotBlank(message = "厂商名不能为空")
    private String vendorName;

    @ApiModelProperty("厂商网址")
    @NotBlank(message = "厂商网址不能为空")
    private String vendorUrl;

    @ApiModelProperty("厂商描述")
    @NotBlank(message = "厂商描述不能为空")
    private String vendorDescription;

}
