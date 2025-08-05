package com.ruoyi.system.domain.dto;

import java.util.Date;

import com.ruoyi.system.domain.base.PageBase;
import com.ruoyi.system.group.InsertGroup;
import com.ruoyi.system.group.UpdateGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


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
public class FaultHandlingDto extends PageBase {

    @ApiModelProperty("主键id")
    @NotBlank(message = "资源id不能为空", groups = UpdateGroup.class)
   private String id;
    
    @ApiModelProperty("用户id")
   private String userId;
    
    @ApiModelProperty("资源id")
    @NotBlank(message = "资源id不能为空", groups = InsertGroup.class)
   private String resourcesId;
    
    @ApiModelProperty("故障描述")
    @NotBlank(message = "故障描述不能为空", groups = InsertGroup.class)
   private String faultDescription;
    
    @ApiModelProperty("处理状态")
   private String status;
    


}
