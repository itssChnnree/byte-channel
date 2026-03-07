package com.ruoyi.system.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 服务器资源自动续费表(ServerResourcesRenewal)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-05 23:13:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerResourcesRenewalDto {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("资源id")
    private String resourcesId;

    @ApiModelProperty("续费开关,0为关，1为开")
    private Integer renewalSwitch;

    @ApiModelProperty("开启用户")
    private String userId;

    @ApiModelProperty("续费周期")
    private String renewalPeriod;

    @ApiModelProperty("续费时间")
    private Date renewalTime;

    @ApiModelProperty("优惠卷")
    private String renewalPromo;

    @ApiModelProperty("是否支持优惠码失效后原价续费")
    private Integer isAgreeOriginalPrice;

    @ApiModelProperty("接受涨价的百分比")
    private Integer acceptablePriceIncreasePct;

    @ApiModelProperty("价格快照")
    private BigDecimal currentPriceSnapshot;

}
