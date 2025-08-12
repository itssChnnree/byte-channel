package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * [直接购买商品]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderByCommodityDto {

    @ApiModelProperty("商品id")
    @NotBlank(message = "商品id为空")
    private String commodityId;

    @ApiModelProperty("购买数量")
    @NotNull(message = "商品数量为空")
    @Min(value = 1, message = "商品数量不能小于1")
    private Integer num;


}
