package com.ruoyi.system.domain.dto;

import com.ruoyi.system.anno.InitializeWith;
import com.ruoyi.system.constant.PaymentPeriodConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * [续费订单创建]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderByRenewalDto {

    @ApiModelProperty("资源id")
    @NotBlank(message = "资源id为空")
    private String resourcesId;

    @ApiModelProperty("推广码")
    private String promoCode;

    @ApiModelProperty("原始价格")
    @NotNull(message = "原始价格为空")
    private BigDecimal originalPrice;

    //付款周期
    @ApiModelProperty("付款周期")
    @InitializeWith(stringValue = PaymentPeriodConstant.MONTHLY)
    private String payCycle;


}
