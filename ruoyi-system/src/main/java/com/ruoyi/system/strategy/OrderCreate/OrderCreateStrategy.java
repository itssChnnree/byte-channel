package com.ruoyi.system.strategy.OrderCreate;


import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.entity.Commodity;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.entity.PromoCodeRecords;

/**
 * @author a1152
 */
public interface OrderCreateStrategy {


    //创建订单
    Order createOrder(OrderByCommodityDto orderByCommodityDto, Commodity normalCommodity, PromoCodeRecords promoCodeRecords);


}
