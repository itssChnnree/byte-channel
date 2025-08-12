package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 购物车购买]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderByShoppingCartDto {

    @ApiModelProperty("商品id")
    private List<String> shoppingCartIdList;
}
