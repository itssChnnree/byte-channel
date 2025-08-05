package com.ruoyi.system.domain.dto;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * 钱包余额表(WalletBalance)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletBalanceDto{

    @ApiModelProperty("主键id")
   private String id;
    
    @ApiModelProperty("用户id")
   private String userId;
    
    @ApiModelProperty("余额")
   private Double balance;
    
    @ApiModelProperty("邀请人数")
   private Integer inviteesNumber;
    
    @ApiModelProperty("邀请奖励金")
   private Double referralBonusCredits;
    
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
