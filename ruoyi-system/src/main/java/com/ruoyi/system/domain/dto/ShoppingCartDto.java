package com.ruoyi.system.domain.dto;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * 用户购物车(ShoppingCart)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-06 23:43:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartDto {

    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("商品id")
    @NotBlank(message = "商品id为空")
    private String commodityId;

    @ApiModelProperty("商品数量")
    @NotNull(message = "商品数量为空")
    private Integer commodityNum;

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
