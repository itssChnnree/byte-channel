package com.ruoyi.system.domain.vo;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 服务器资源表(ServerResources)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerResourcesVo{

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("服务器ip")
    private String resourcesIp;

    @ApiModelProperty("服务器端口")
    private String resourcesPort;

    @ApiModelProperty("服务器密码")
    private String resourcesPassword;

    @ApiModelProperty("服务器状态")
    private String resourcesStatus;

    @ApiModelProperty("资源当前租户")
    private String resourceTenant;

    @ApiModelProperty("租赁到期时间")
    private Date leaseExpirationTime;

    @ApiModelProperty("售卖状态")
    private String salesStatus;

    @ApiModelProperty("可售状态，可售状态,0为下架，1为上架")
    private Integer availableStatus;

    @ApiModelProperty("资源所属云服务商")
    private String cloudServiceProvider;

    @ApiModelProperty("资源所属云服务商账号")
    private String cloudUser;

    @ApiModelProperty("节点导入链接")
    private String nodeUrl;

    @ApiModelProperty("节点端口")
    private String nodePort;

    @ApiModelProperty("vless协议publickey")
    private String publicBrokerKey;

    @ApiModelProperty("vless协议publicKey")
    private String publicKey;

    @ApiModelProperty("vless协议劫持域名")
    private String sni;

    @ApiModelProperty("vless协议短id")
    private String shortId;

    @ApiModelProperty("vless协议用户id")
    private String userId;

    @ApiModelProperty("所属商品id")
    private String commodityId;

    @ApiModelProperty("0为未删除，1为已删除")
    private Integer isDeleted;

    @ApiModelProperty("创建人")
    private String createUser;

    @ApiModelProperty("修改人")
    private String updateUser;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;
}
