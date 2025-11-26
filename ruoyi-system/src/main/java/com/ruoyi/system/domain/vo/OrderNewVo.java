package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * [新购订单-订单资源详情]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/11/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderNewVo implements OrderResourcesVo{

    @ApiModelProperty("商品名")
    private String commodityName;

    @ApiModelProperty("商品编号")
    private String commodityId;

    @ApiModelProperty("商品描述")
    private String commodityDesc;

    @ApiModelProperty("带宽")
    private String bandwidth;

    @ApiModelProperty("商品类别id")
    private String categoryId;

    @ApiModelProperty("商品类别名称")
    private String categoryName;

    @ApiModelProperty("商品类别描述")
    private String categoryDesc;

    @ApiModelProperty("资源ip")
    private String ip;

    @ApiModelProperty("用户id")
    private String userId;


}
