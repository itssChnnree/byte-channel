package com.ruoyi.system.domain.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 订单支付方式表(OrderPayType)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-23 21:16:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPayTypeVo {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("购买用户id")
    private String userId;

    @ApiModelProperty("订单id")
    private String orderId;

    @ApiModelProperty("微信订单id")
    private String wxOrderId;

    @ApiModelProperty("支付宝订单id")
    private String alipayOrderId;

    @ApiModelProperty("是否进行最终核对，0为否，1为是")
    private Integer isCheck;

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
