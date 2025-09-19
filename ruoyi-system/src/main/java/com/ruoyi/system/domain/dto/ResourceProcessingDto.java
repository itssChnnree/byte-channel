package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/9/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceProcessingDto {


    @ApiModelProperty("商品id")
    @NotBlank(message = "商品id不能为空")
    private String commodityId;

    @ApiModelProperty("服务器ip")
    @NotBlank(message = "商品id不能为空")
    private String resourcesIp;

    @ApiModelProperty("账号id")
    @NotBlank(message = "服务器ip不能为空")
    private String vendorAccountId;

    @ApiModelProperty("服务器账号")
    @NotBlank(message = "服务器账号不能为空")
    private String resourcesUserName;

    @ApiModelProperty("服务器密码")
    @NotBlank(message = "服务器密码不能为空")
    private String resourcesPassword;

    @ApiModelProperty("服务器端口")
    @NotBlank(message = "服务器端口不能为空")
    private String resourcesPort;


}
