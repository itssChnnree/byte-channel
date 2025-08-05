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
import javax.validation.constraints.NotNull;


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
public class CommodityCategoryDto extends PageBase {

    @ApiModelProperty("主键id")
    @NotBlank(message = "主键id不能为空",groups = {UpdateGroup.class})
    private String id;
    
    @ApiModelProperty("分类名")
    @NotBlank(message = "分类名不能为空",groups = {InsertGroup.class})
    private String categoryName;

    @ApiModelProperty("分类描述")
    @NotBlank(message = "分类描述不能为空",groups = {InsertGroup.class})
    private String summary;
    
    @ApiModelProperty("状态")
    private String status;
    
    @ApiModelProperty("可售状态，0为下架，1为上架")
    @NotNull(message = "可售状态不能为空")
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
