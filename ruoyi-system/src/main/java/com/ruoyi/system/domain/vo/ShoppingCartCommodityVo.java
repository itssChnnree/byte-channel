package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [购物车中商品返回类]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/9
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ShoppingCartCommodityVo {

    @ApiModelProperty("购物车id")
    private String shoppingCartId;

    @ApiModelProperty("商品id")
    private String commodityId;

    @ApiModelProperty("商品名称")
    private String commodityName;

    @ApiModelProperty("添加商品数量")
    private String commodityNum;

    @ApiModelProperty("是否无库存")
    private String isNoStock;

    @ApiModelProperty("购买数是否大于库存数")
    private String isBuyNumMoreThanStockNum;

    @ApiModelProperty("商品分类Id")
    private String commodityCategoryId;

    @ApiModelProperty("商品分类名称")
    private String commodityCategoryName;

    @ApiModelProperty("商品价格")
    private String commodityPrice;

    @ApiModelProperty("商品折扣后价格")
    private String commodityDiscountPrice;


}
