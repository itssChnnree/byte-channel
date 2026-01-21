package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


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
public class PromoRecordsPageVo {

    @ApiModelProperty("主键id")
   private String id;
    
    @ApiModelProperty("推荐人用户id")
   private String userId;
    
    @ApiModelProperty("被推荐人名称")
   private String referralsUserName;
    
    @ApiModelProperty("推广码记录id")
   private String promoCodeRecordsId;

    @ApiModelProperty("推广码")
    private String promoCodeRecords;
    
    @ApiModelProperty("返现金额")
   private Double cashbackAmount;
    
    @ApiModelProperty("状态")
   private String status;
    
    @ApiModelProperty("被推荐人购买金额")
   private Double purchaseAmount;
    
    @ApiModelProperty("返现比例")
   private String cashbackPercentage;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    

    

}
