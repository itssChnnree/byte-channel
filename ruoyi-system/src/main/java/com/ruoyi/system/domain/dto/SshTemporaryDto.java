package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * [ssh连接信息]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SshTemporaryDto {

    @ApiModelProperty("ip")
    @NotBlank(message = "ip不能为空")
    private String ip;

    @ApiModelProperty("端口")
    @NotBlank(message = "端口不能为空")
    private String port;

    @ApiModelProperty("用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @ApiModelProperty("密码")
    @NotBlank(message = "密码不能为空")
    private String password;
}
