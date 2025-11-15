package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [订单信息详情]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/9/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoVo {

    //订单ID
    @ApiModelProperty("订单ID")
    private String orderId;

    //商品名
    @ApiModelProperty("商品名")
    private String commodityName;

    //商品描述
    @ApiModelProperty("商品描述")
    private String commodityDesc;

    //带宽
    @ApiModelProperty("带宽")
    private String bandwidth;

    //付款周期
    @ApiModelProperty("付款周期")
    private String payCycle;

    //推荐码
    @ApiModelProperty("推荐码")
    private String promoCode;

    //订单总价
    @ApiModelProperty("订单总价")
    private String price;

    //折扣
    @ApiModelProperty("折扣")
    private String discountPrice;

    //订单状态
    @ApiModelProperty("订单状态")
    private String status;

    //订单类型
    @ApiModelProperty("订单类型")
    private String orderType;

    @ApiModelProperty("续费资源ip")
    private String ip;
}
