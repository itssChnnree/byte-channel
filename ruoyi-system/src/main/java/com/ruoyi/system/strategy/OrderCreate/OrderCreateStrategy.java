package com.ruoyi.system.strategy.OrderCreate;


import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.entity.Commodity;
import com.ruoyi.system.domain.entity.Order;

/**
 * @author a1152
 */
public interface OrderCreateStrategy {

    public Order createOrder(OrderByCommodityDto orderByCommodityDto, Commodity normalCommodity);


}
