package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.base.PageBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;

/**
 * 中转资源表(RelayResource)入参DTO
 * 继承PageBase支持分页，resourceIpLike用于模糊搜索
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelayResourceDto extends PageBase {

    @ApiModelProperty("主键ID")
    private String id;

    @ApiModelProperty("所属中转节点ID")
    private String nodeId;

    @ApiModelProperty("厂商账号ID")
    private String vendorAccountId;

    @ApiModelProperty("vpsIp")
    private String resourceIp;

    @ApiModelProperty("vps端口")
    private String resourcePort;

    @ApiModelProperty("用户名")
    private String resourceUserName;

    @ApiModelProperty("资源密码/密钥")
    private String resourcePassword;

    @ApiModelProperty("可中转数量")
    private Integer transferableQuantity;

    @ApiModelProperty("启用状态 0停用 1启用")
    private Integer availableStatus;

    @ApiModelProperty("资源IP模糊搜索")
    private String resourceIpLike;
}
