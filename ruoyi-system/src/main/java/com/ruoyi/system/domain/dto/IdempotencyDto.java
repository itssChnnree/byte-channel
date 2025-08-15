package com.ruoyi.system.domain.dto;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * 幂等性控制表(Idempotency)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-15 14:25:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdempotencyDto{

    @ApiModelProperty("主键id")
   private String id;
    
    @ApiModelProperty("请求唯一标识（业务方生成）")
   private String requestId;
    
    @ApiModelProperty("业务类型（如：payment/order）")
   private String businessType;
    
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
