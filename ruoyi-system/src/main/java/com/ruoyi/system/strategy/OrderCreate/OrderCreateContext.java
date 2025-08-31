package com.ruoyi.system.strategy.OrderCreate;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.entity.Commodity;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.entity.PromoCodeRecords;

import java.math.BigDecimal;


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

    public BigDecimal calculatePrice(OrderByCommodityDto orderByCommodityDto, Commodity normalCommodity, PromoCodeRecords promoCodeRecords) {
        BigDecimal price;
        //存在折扣价则使用折扣价，不存在则使用原价
        if (ObjectUtil.isNotEmpty(normalCommodity.getCommodityDiscountedPrice())){
            price = normalCommodity.getCommodityDiscountedPrice();
        }else {
            price = normalCommodity.getCommodityPrice();
        }
        return orderCreateStrategy.calculatePrice(orderByCommodityDto.getNum(), ObjectUtil.isNotEmpty(promoCodeRecords),price);
    }



}
