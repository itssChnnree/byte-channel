package com.ruoyi.system.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 订单表(Order)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:02
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSystemVo extends OrderVo{


    /**
     * 第三方支付id
     */
    @ApiModelProperty(value = "第三方支付id")
    private String paymentId;

    /**
     * 支付方式
     */
    @ApiModelProperty(value = "支付方式")
    private String paymentType;

}
