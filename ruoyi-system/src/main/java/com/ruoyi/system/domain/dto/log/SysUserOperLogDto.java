package com.ruoyi.system.domain.dto.log;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * [用户操作日志查询入参]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2024/2/23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("用户操作日志查询入参")
public class SysUserOperLogDto {

    @ApiModelProperty("查询起始时间")
    private String startTime;

    @ApiModelProperty("查询结束时间")
    private String endTime;

    @ApiModelProperty("查询条件")
    private String query;

    @ApiModelProperty("索引名")
    private String index;

    @ApiModelProperty("过滤条件集合")
    private List<OperFilterDTO> filterList;

    @ApiModelProperty("是否开启高亮，1为开启")
    private int openHighLight;

    @ApiModelProperty("分页配置，当前页")
    private int page;

    @ApiModelProperty("分页配置，每页数量")
    private int size;

    @ApiModelProperty("排序字段")
    private String sortField;

    @ApiModelProperty("排序方式，1为升序，2为降序")
    private int sortType;

}
