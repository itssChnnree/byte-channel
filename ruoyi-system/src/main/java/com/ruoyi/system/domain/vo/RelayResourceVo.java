package com.ruoyi.system.domain.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 中转资源表(RelayResource)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10 14:57:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelayResourceVo {

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

    @ApiModelProperty("是否可用")
    private Integer availableStatus;

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

    @ApiModelProperty("状态（暂时弃用）")
    private String status;

    @ApiModelProperty("所属节点名称")
    private String nodeName;


}
