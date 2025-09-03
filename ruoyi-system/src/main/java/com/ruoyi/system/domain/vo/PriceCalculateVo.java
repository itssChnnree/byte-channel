package com.ruoyi.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [价格计算反参]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/9/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceCalculateVo {

    //按月付款
    private String monthlyPrice;

    //按月付款折扣
    private String monthlyDiscountPrice;

    //按季付款
    private String quarterlyPrice;

    //按季付款折扣
    private String quarterlyDiscountPrice;

    //按年付款
    private String yearlyPrice;

    //按年付款折扣
    private String yearlyDiscountPrice;
}
