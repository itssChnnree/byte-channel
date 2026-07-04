package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.base.PageBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;

/**
 * 中转节点表(RelayNode)入参DTO
 * 继承PageBase支持分页，nodeNameLike用于模糊搜索
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelayNodeDto extends PageBase {

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

    @ApiModelProperty("启用状态 0停用 1启用")
    private Integer availableStatus;

    @ApiModelProperty("节点名称模糊搜索")
    private String nodeNameLike;
}
