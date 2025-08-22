package com.ruoyi.system.strategy.OrderCreate;

import cn.hutool.extra.spring.SpringUtil;
import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.entity.Commodity;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.entity.PromoCodeRecords;


/**
 * [用户通知上下文类]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/6/24
 */
public class OrderCreateContext {

    private OrderCreateStrategy orderCreateStrategy;

    public OrderCreateContext(String orderCreateType) {
        orderCreateStrategy = SpringUtil.getBean(orderCreateType);
    }

    public Order createOrder(OrderByCommodityDto orderByCommodityDto, Commodity normalCommodity, PromoCodeRecords promoCodeRecords) {
        return orderCreateStrategy.createOrder(orderByCommodityDto, normalCommodity, promoCodeRecords);
    }



}
