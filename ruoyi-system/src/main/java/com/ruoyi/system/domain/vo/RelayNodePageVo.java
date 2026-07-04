package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelayNodePageVo {

    @ApiModelProperty("主键ID")
    private String id;

    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("资源数量")
    private Integer resourceCount;

    @ApiModelProperty("启用状态 0停用 1启用")
    private Integer availableStatus;

    @ApiModelProperty("创建时间")
    private Date createTime;
}
