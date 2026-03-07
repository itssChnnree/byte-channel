package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 推广记录表(PromoRecords)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromoRecordsVo{

    @ApiModelProperty("主键id")
   private String id;
    
    @ApiModelProperty("推荐人用户id")
   private String userId;
    
    @ApiModelProperty("被推荐人用户id")
   private String referralsUserId;
    
    @ApiModelProperty("推广码记录id")
   private String promoCodeRecordsId;
    
    @ApiModelProperty("返现金额")
   private BigDecimal cashbackAmount;
    
    @ApiModelProperty("状态")
   private String status;
    
    @ApiModelProperty("被推荐人购买金额")
   private BigDecimal purchaseAmount;
    
    @ApiModelProperty("返现比例")
   private String cashbackPercentage;
    
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
