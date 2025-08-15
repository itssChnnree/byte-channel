package com.ruoyi.system.domain.dto;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * 订单商品(OrderCommodity)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-15 10:01:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCommodityDto{

    @ApiModelProperty("主键id")
   private String id;
    
    @ApiModelProperty("购买用户id")
   private String userId;
    
    @ApiModelProperty("订单id")
   private String orderId;
    
    @ApiModelProperty("购买数量")
   private Integer purchaseQuantity;
    
    @ApiModelProperty("交付数量")
   private Integer orderQuantity;
    
    @ApiModelProperty("商品id")
   private String commodityId;
    
    @ApiModelProperty("0为未删除，1为已删除")
   private Integer isDeleted;
    
    @ApiModelProperty("创建人")
   private String createUser;
    
    @ApiModelProperty("修改人")
   private String updateUser;
    
    @ApiModelProperty("创建时间")
   private Date createTime;
    
    @ApiModelProperty("修改时间")
   private Date updateTime;
    

}
