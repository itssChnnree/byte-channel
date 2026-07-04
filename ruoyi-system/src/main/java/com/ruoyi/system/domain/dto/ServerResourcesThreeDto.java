package com.ruoyi.system.domain.dto;

import com.ruoyi.system.group.InsertGroup;
import com.ruoyi.system.group.UpdateGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;


/**
 * 第三方服务器资源表(ServerResources)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerResourcesThreeDto {

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


    @ApiModelProperty("vless协议publickey")
    @NotBlank(message = "vless协议publickey不能为空")
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


}
