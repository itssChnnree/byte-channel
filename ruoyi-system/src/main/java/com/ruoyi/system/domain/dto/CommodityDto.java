package com.ruoyi.system.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.ruoyi.system.anno.InitializeWith;
import com.ruoyi.system.domain.base.PageBase;
import com.ruoyi.system.group.UpdateGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * 商品表(Commodity)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:33
 */

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommodityDto extends PageBase {

    @ApiModelProperty("商品编号")
    @NotBlank(message = "商品编号不能为空", groups = UpdateGroup.class)
   private String id;
    
    @ApiModelProperty("商品名")
    @NotBlank(message = "商品名不能为空")
   private String commodityName;
    
    @ApiModelProperty("价格")
    @NotNull(message = "商品价格不能为空")
    private BigDecimal commodityPrice;
    
    @ApiModelProperty("折后价")
    private BigDecimal commodityDiscountedPrice;
    
    @ApiModelProperty("商品类别id")
    @NotBlank(message = "商品类别id不能为空")
    private String categoryId;
    
    @ApiModelProperty("超卖配置数")
    @InitializeWith(intValue = 10)
    private Integer oversoldConfigurations;
    
    @ApiModelProperty("已超卖数")
    @InitializeWith(intValue = 0)
    private Integer oversold;
    
    @ApiModelProperty("库存")
    @InitializeWith(intValue = 0)
    private Integer inventory;
    
    @ApiModelProperty("告警阈值")
    @InitializeWith(intValue = 5)
    private Integer alarmThreshold;
    
    @ApiModelProperty("描述")
    @NotBlank(message = "商品描述不能为空")
    private String description;
    
    @ApiModelProperty("类别")
    @NotBlank(message = "商品类别不能为空")
    private String category;
    
    @ApiModelProperty("为未删除，1为已删除")
    @InitializeWith(intValue = 0)
    private Integer isDeleted;
    
    @ApiModelProperty("状态")
    private String status;
    
    @ApiModelProperty("可售状态,0为下架，1为上架")
    @InitializeWith(intValue = 0)
    private Integer availableStatus;
}
