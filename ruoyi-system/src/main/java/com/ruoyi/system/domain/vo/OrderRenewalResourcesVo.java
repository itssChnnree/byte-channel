package com.ruoyi.system.domain.vo;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 订单-续费资源表(OrderRenewalResources)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-15 23:46:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRenewalResourcesVo{

    @ApiModelProperty("主键id")
   private String id;
    
    @ApiModelProperty("订单id")
   private String orderId;
    
    @ApiModelProperty("资源ip")
   private String resourcesIp;
    
    @ApiModelProperty("资源id")
   private String resourcesId;
    
    @ApiModelProperty("购买用户id")
   private String userId;

    @ApiModelProperty("商品id")
    private String commodityId;

}
