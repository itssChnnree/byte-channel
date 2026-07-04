package com.ruoyi.system.domain.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 中转节点表(RelayNode)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10 14:57:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelayNodeVo {

    @ApiModelProperty("主键ID")
    private String id;

    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("可用域名")
    private String serverNames;

    @ApiModelProperty("劫持域名")
    private String dest;

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

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("启用状态 0停用 1启用")
    private Integer availableStatus;

}
