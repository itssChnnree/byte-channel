package com.ruoyi.system.domain.vo;

import java.util.Date;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




/**
 * 商品类别(CommodityCategory)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommodityCategoryVo{

    @ApiModelProperty("主键id")
   private String id;
    
    @ApiModelProperty("分类名")
   private String categoryName;

    @ApiModelProperty("分类描述")
    private String summary;
    
    @ApiModelProperty("状态")
   private String status;

    @ApiModelProperty("排序")
    private Integer sort;
    
    @ApiModelProperty("可售zh")
   private Integer availableStatus;
    
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
