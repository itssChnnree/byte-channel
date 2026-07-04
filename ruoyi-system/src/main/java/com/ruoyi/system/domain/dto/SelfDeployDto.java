package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 自助部署节点入参DTO
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelfDeployDto {

    @ApiModelProperty("服务器IP")
    @NotBlank(message = "服务器IP不能为空")
    private String ip;

    @ApiModelProperty("SSH端口")
    private String port;

    @ApiModelProperty("SSH账号")
    @NotBlank(message = "SSH账号不能为空")
    private String username;

    @ApiModelProperty("SSH密码")
    @NotBlank(message = "SSH密码不能为空")
    private String password;

    @ApiModelProperty("Xray监听端口（10000-65535，留空则随机）")
    private Integer xrayPort;

    @ApiModelProperty("验证码uuid")
    @NotBlank(message = "验证码uuid不能为空")
    private String uuid;

    @ApiModelProperty("验证码")
    @NotBlank(message = "验证码不能为空")
    private String code;

    @ApiModelProperty("下一跳密码")
    public String nextPassword;

    @ApiModelProperty("中转节点增加模式，1为全量模式，2为增量模式")
    public Integer nextType;
}
