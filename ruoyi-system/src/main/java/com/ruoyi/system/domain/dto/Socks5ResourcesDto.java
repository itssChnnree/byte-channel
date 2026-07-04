package com.ruoyi.system.domain.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * socks5资源记录(Socks5Resources)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-06-29 22:23:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Socks5ResourcesDto {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("服务器ip")
    private String resourcesIp;

    @ApiModelProperty("socks5端口")
    private String socks5Port;

    @ApiModelProperty("socks5账号")
    private String socks5UserName;

    @ApiModelProperty("socks5密码")
    private String socks5Password;

    @ApiModelProperty(" 查询密码")
    private String password;

}
