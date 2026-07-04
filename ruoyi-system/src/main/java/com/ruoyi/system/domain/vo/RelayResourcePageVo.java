package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * 中转资源分页查询反参VO
 * 含LEFT JOIN relay_node查询的nodeName
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelayResourcePageVo {

    @ApiModelProperty("主键ID")
    private String id;

    @ApiModelProperty("所属中转节点ID")
    private String nodeId;

    @ApiModelProperty("所属节点名称")
    private String nodeName;

    @ApiModelProperty("厂商账号ID")
    private String vendorAccountId;

    @ApiModelProperty("vpsIp")
    private String resourceIp;

    @ApiModelProperty("vps端口")
    private String resourcePort;

    @ApiModelProperty("用户名")
    private String resourceUserName;

    @ApiModelProperty("可中转数量")
    private Integer transferableQuantity;

    @ApiModelProperty("启用状态 0停用 1启用")
    private Integer availableStatus;

    @ApiModelProperty("创建时间")
    private Date createTime;
}
