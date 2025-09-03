package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 商品表(Commodity)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommodityVo{

    @ApiModelProperty("商品编号")
   private String id;
    
    @ApiModelProperty("商品名")
   private String commodityName;
    
    @ApiModelProperty("价格")
   private BigDecimal commodityPrice;
    
    @ApiModelProperty("折后价")
   private BigDecimal commodityDiscountedPrice;

    @JsonIgnore
    private String businessSuitable;

    @ApiModelProperty("带宽")
    private String bandwidth;

    @ApiModelProperty("商品适用业务")
    private List<String> businessSuitableList;
    
    @ApiModelProperty("商品类别id")
   private String categoryId;
    
    @ApiModelProperty("超卖配置数")
   private Integer oversoldConfigurations;
    
    @ApiModelProperty("已超卖数")
   private Integer oversold;
    
    @ApiModelProperty("库存")
   private Integer inventory;
    
    @ApiModelProperty("告警阈值")
   private Integer alarmThreshold;
    
    @ApiModelProperty("描述")
   private String description;

    
    @ApiModelProperty("状态")
   private String status;
    
    @ApiModelProperty("可售状态,0为下架，1为上架")
   private Integer availableStatus;


    @ApiModelProperty("是否有库存")
    private Boolean hasStock;
    

}
