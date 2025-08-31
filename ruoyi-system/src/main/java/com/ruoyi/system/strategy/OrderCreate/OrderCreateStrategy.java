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


    //创建订单
    Order createOrder(OrderByCommodityDto orderByCommodityDto, Commodity normalCommodity, PromoCodeRecords promoCodeRecords);


    /**
     * [计算价格]
     * @author 陈湘岳 2025/8/30
     * @param num
     * @param havePromoCode
     * @param price
     * @return java.math.BigDecimal
     **/
    BigDecimal calculatePrice(int num, Boolean havePromoCode, BigDecimal price);
}
