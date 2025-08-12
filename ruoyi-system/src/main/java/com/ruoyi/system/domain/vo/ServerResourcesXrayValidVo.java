package com.ruoyi.system.domain.vo;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 资源状态检查节点分布(ServerResourcesXrayValid)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-05 17:32:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerResourcesXrayValidVo{

    @ApiModelProperty("主键id")
   private String id;
    
    @ApiModelProperty("资源id")
   private String resourcesId;
    
    @ApiModelProperty("校验服务器ip")
   private String webIp;
    
    @ApiModelProperty("校验服务器-web服务端口")
   private String webPort;
    
    @ApiModelProperty("校验服务器-xray代理端口")
   private String xrayPort;
    
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
