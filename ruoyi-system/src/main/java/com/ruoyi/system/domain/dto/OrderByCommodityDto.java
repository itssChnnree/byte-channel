package com.ruoyi.system.domain.dto;

import com.ruoyi.system.anno.InitializeWith;
import com.ruoyi.system.strategy.OrderCreate.OrderConstant;
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

    @ApiModelProperty("推广码")
    private String promoCode;

    //付款周期
    @ApiModelProperty("付款周期")
    @InitializeWith(stringValue = OrderConstant.MONTHLY)
    private String payCycle;


}
