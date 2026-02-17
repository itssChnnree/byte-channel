package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 钱包余额明细表(WalletBalanceDetail)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletBalanceDetailVo{

    @ApiModelProperty("主键id")
   private String id;
    
    @ApiModelProperty("用户id")
   private String userId;
    
    @ApiModelProperty("变更金额")
    @JsonIgnore
   private BigDecimal changeAmount;

    @ApiModelProperty("变更时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    @ApiModelProperty("变更类型")
   private String type;

    @ApiModelProperty("变更前金额")
    @JsonIgnore
    private BigDecimal nowAmount;

    public String getChangeAmountStr(){
        return changeAmount.toString();
    }

    public String getNowAmountStr(){
        return nowAmount.toString();
    }

}
