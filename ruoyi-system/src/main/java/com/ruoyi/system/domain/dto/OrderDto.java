package com.ruoyi.system.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.system.domain.base.PageBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * 订单表(Order)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto extends PageBase {

    @ApiModelProperty("主键id")
    private String id;
    
    @ApiModelProperty("购买用户id")
    private String userId;
    
    @ApiModelProperty("订单金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "交易描述")
    private String description;
    
    @ApiModelProperty("订单状态")
    private String status;
    
    @ApiModelProperty("下单时间")
    private Date orderTime;

    @ApiModelProperty("下单创建开始时间")
    private String orderCreateTime;

    @ApiModelProperty("订单创建完成时间")
    private String orderEndTime;

    @ApiModelProperty("订单类型")
    private String orderType;

    @ApiModelProperty("付款周期")
    private String paymentPeriod;

    @ApiModelProperty(value = "第三方支付id")
    private String paymentId;

    @ApiModelProperty(value = "支付方式")
    private String paymentType;
    

}
