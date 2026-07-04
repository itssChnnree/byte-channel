package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * 中转端口分页查询反参VO
 * 含LEFT JOIN relay_resource和relay_node查询的resourceIp和nodeName
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelayPortPageVo {

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

    @ApiModelProperty("所属节点名称")
    private String nodeName;

    @ApiModelProperty("中转资源IP")
    private String resourceIp;

    @ApiModelProperty("创建时间")
    private Date createTime;
}
