package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.base.PageBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;

/**
 * 中转端口表(RelayPort)入参DTO
 * 继承PageBase支持分页，keyword用于端口号模糊搜索，nodeId/resourceIp用于筛选
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelayPortDto extends PageBase {

    @ApiModelProperty("主键ID")
    private String id;

    @ApiModelProperty("所属中转资源ID")
    private String relayResourceId;

    @ApiModelProperty("目标资源ID（转发目标）")
    private String targetResourceId;

    @ApiModelProperty("转发端口")
    private String port;

    @ApiModelProperty("协议类型（vmess/vless/trojan/ss）")
    private String protocol;

    @ApiModelProperty("传输方式（ws/tcp/grpc/h2）")
    private String transport;

    @ApiModelProperty("传输路径")
    private String path;

    @ApiModelProperty("加密方式（none/tls/xtls）")
    private String security;

    @ApiModelProperty("公钥")
    private String publicKey;

    @ApiModelProperty("SNI域名")
    private String sni;

    @ApiModelProperty("短ID")
    private String shortId;

    @ApiModelProperty("节点端口")
    private String nodePort;

    @ApiModelProperty("端口号模糊搜索")
    private String keyword;

    @ApiModelProperty("中转节点筛选")
    private String nodeId;

    @ApiModelProperty("中转资源IP筛选")
    private String resourceIp;
}
