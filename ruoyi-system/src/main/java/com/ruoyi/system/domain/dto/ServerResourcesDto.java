package com.ruoyi.system.domain.dto;

import java.util.Date;

import com.ruoyi.system.group.InsertGroup;
import com.ruoyi.system.group.UpdateGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


/**
 * 服务器资源表(ServerResources)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerResourcesDto{

    @ApiModelProperty("主键id")
    @NotBlank(message = "主键id不能为空",
            groups = {UpdateGroup.class})
    private String id;
    
    @ApiModelProperty("服务器ip")
    @Pattern(
            regexp = "^(?:(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\.){3}(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])$",
            message = "必须是有效的IPv4地址",
            groups = {InsertGroup.class})
    @NotBlank(message = "服务器ip不能为空")
    private String resourcesIp;
    
    @ApiModelProperty("ssh面板服务器端口")
    @NotBlank(message = "服务器端口不能为空")
    @Pattern(regexp = "^([1-9]\\d{0,3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$",
            message = "端口号必须是1-65535之间的整数",
            groups = {InsertGroup.class})
    private String resourcesPort;

    @ApiModelProperty("服务器密码")
    @NotBlank(message = "服务器密码不能为空")
    private String resourcesPassword;
    
    @ApiModelProperty("资源当前租户")
    private String resourceTenant;
    
    @ApiModelProperty("租赁到期时间")
    private Date leaseExpirationTime;

    @ApiModelProperty("服务器状态")
    private String resourcesStatus;
    
    @ApiModelProperty("售卖状态")
    private String salesStatus;
    
    @ApiModelProperty("可售状态，可售状态,0为下架，1为上架")
    @NotNull(message = "可售状态不能为空")
    private Integer availableStatus;

    @ApiModelProperty("资源所属云服务商账号id")
    private String vendorAccountId;
    
    @ApiModelProperty("节点导入链接")
    @NotBlank(message = "导入链接不能为空")
    private String nodeUrl;

    @ApiModelProperty("vless协议publickey")
    private String publicBrokerKey;

    @ApiModelProperty("vless协议劫持域名")
    @NotBlank(message = "vless协议劫持域名不能为空")
    private String sni;

    @ApiModelProperty("vless协议短id")
    @NotBlank(message = "vless协议短id不能为空")
    private String shortId;

    @ApiModelProperty("vless协议用户id")
    @NotBlank(message = "vless协议用户id不能为空")
    private String userId;
    
    @ApiModelProperty("代理节点端口")
    @NotBlank(message = "代理节点端口不能为空")
    private String nodePort;
    
    @ApiModelProperty("所属商品id")
    @NotBlank(message = "所属商品id不可为空")
    private String commodityId;
}
