package com.ruoyi.system.domain.vo;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 服务器资源表(ServerResourcesRenewal)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-10-28 10:48:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerResourcesRenewalVo{

    @ApiModelProperty("主键id")
   private String id;
    
    @ApiModelProperty("资源id")
   private String resourcesId;
    
    @ApiModelProperty("续费开关,0为关，1为开")
   private Integer renewalSwitch;
    
    @ApiModelProperty("开启用户")
   private String userId;
    
    @ApiModelProperty("续费周期（月）")
   private Integer renewalPeriod;
    
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
    
    @ApiModelProperty("状态（暂时弃用）")
   private String status;
    

}
