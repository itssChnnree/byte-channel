package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [资源管理页商品详情vo]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/9/19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourcesCommodityVo {

    @ApiModelProperty("商品名称")
    private String commodityName;

    @ApiModelProperty("商品描述")
    private String commodityDescription;

    @ApiModelProperty("商品价格")
    private String commodityPrice;

    @ApiModelProperty("商品分类")
    private String commodityCategory;

    @ApiModelProperty("商品分类id")
    private String commodityCategoryId;

    @ApiModelProperty("商品分类描述")
    private String commodityCategorySummary;

    @ApiModelProperty("可售状态,0为下架，1为上架")
    private Integer availableStatus;
}
