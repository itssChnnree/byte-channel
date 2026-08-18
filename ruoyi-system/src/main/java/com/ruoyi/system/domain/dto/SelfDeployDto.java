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

    @ApiModelProperty("部署类型：entry=新建独立节点，socks5=SOCKS5中转，vless=VLESS中转")
    public String type;

    @ApiModelProperty("中转部署方式，1为覆盖中转（full），2为新增中转（incremental），仅socks5/vless中转必填")
    public Integer nextType;

    @ApiModelProperty("下游SOCKS5 连接串（格式 ip:端口:账号:密码，socks5中转必填）")
    public String downstreamSocks5;
}
