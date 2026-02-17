package com.ruoyi.system.domain.dto.log;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * [过滤入参]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2024/2/27
 */
@ApiModel("过滤入参")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OperFilterDTO {

    /*
     * 需要过滤的字段
     **/

    @ApiModelProperty("操作的字段")
    private String field;

    @ApiModelProperty("操作的类型,1为must,2为must_not,3为should,4为should_not,5为exists，6为not_exists")
    private int operator;

    @ApiModelProperty("操作的值")
    private List<String> value;
}
