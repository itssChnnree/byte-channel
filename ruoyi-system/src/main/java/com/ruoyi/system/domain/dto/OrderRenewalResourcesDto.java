package com.ruoyi.system.domain.dto;

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
 * @date 2025-11-15 23:46:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRenewalResourcesDto{

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
