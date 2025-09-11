package com.ruoyi.system.domain.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 资源暂存表(ResourceAllocationTemporaryStorage)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-11 15:28:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceAllocationTemporaryStorageDto {

    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("服务器ip")
    private String resourcesIp;

    @ApiModelProperty("节点端口")
    private Integer nodePort;

    @ApiModelProperty("公钥")
    private String publicKey;

    @ApiModelProperty("短id")
    private String shortId;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("vless协议劫持域名")
    private String sni;


}
