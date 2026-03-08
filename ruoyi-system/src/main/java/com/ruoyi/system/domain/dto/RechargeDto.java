package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 充值表(WalletBalance)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RechargeDto {

    
    @ApiModelProperty("用户id")
   private String userId;
    
    @ApiModelProperty("充值金额")
   private BigDecimal rechargeAmount;


}
