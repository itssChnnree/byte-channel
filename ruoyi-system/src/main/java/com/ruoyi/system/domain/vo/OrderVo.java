package com.ruoyi.system.domain.vo;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 订单表(Order)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVo{

    @ApiModelProperty("主键id")
   private String id;
    
    @ApiModelProperty("购买用户id")
   private String userId;
    
    @ApiModelProperty("订单金额")
   private Double amount;
    
    @ApiModelProperty("订单状态")
   private String status;
    
    @ApiModelProperty("下单时间")
   private Date orderTime;
    
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
