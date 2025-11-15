package com.ruoyi.system.strategy.OrderCreate;


import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.entity.Commodity;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.entity.PromoCodeRecords;

import java.math.BigDecimal;

/**
 * @author a1152
 */
public interface OrderCreateStrategy {


    //创建新购买订单
    Order createOrder(int num, Commodity normalCommodity, PromoCodeRecords promoCodeRecords);

    //创建续费订单
    Order createRenewalOrder(PromoCodeRecords promoCodeRecords, Commodity normalCommodity);


    /**
     * [计算价格]
     * @author 陈湘岳 2025/8/30
     * @param num 商品数量
     * @param havePromoCode 是否有优惠码
     * @param price 价格
     * @return java.math.BigDecimal
     **/
    BigDecimal calculatePrice(int num, Boolean havePromoCode, BigDecimal price);
}
