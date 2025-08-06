package com.ruoyi.system.domain.vo;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 故障处理流程表(FaultHandling)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaultHandlingVo{

    @ApiModelProperty("主键id")
   private String id;
    
    @ApiModelProperty("用户id")
   private String userId;
    
    @ApiModelProperty("资源id")
   private String resourcesId;
    
    @ApiModelProperty("故障描述")
   private String faultDescription;
    
    @ApiModelProperty("处理状态")
   private String status;

    @ApiModelProperty("服务器状态")
    private String resourcesStatus;
    
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
