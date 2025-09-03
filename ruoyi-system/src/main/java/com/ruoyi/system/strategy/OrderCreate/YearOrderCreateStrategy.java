package com.ruoyi.system.strategy.OrderCreate;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.OrderStatus;
import com.ruoyi.system.constant.OrderTypeConstant;
import com.ruoyi.system.constant.PaymentPeriodConstant;
import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.entity.Commodity;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.entity.PromoCodeRecords;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/12
 */

@Component(PaymentPeriodConstant.YEARLY)
public class YearOrderCreateStrategy implements OrderCreateStrategy{


    @Override
    public Order createOrder(OrderByCommodityDto orderByCommodityDto, Commodity normalCommodity, PromoCodeRecords promoCodeRecords) {
        BigDecimal price = null;
        //存在折扣价则使用折扣价，不存在则使用原价
        if (ObjectUtil.isNotEmpty(normalCommodity.getCommodityDiscountedPrice())){
            price = normalCommodity.getCommodityDiscountedPrice();
        }else {
            price = normalCommodity.getCommodityPrice();
        }
        //计算价格
        BigDecimal total = calculatePrice(orderByCommodityDto.getNum(), ObjectUtil.isNotEmpty(promoCodeRecords), price);

        Order order = new Order();
        order.setUserId(SecurityUtils.getStrUserId());
        order.setAmount(total);
        order.setStatus(OrderStatus.WAIT_PAY);
        order.setOrderTime(new Date());
        order.setDescription("按年订购节点");
        order.setPaymentPeriod(PaymentPeriodConstant.YEARLY);
        order.setOrderType(OrderTypeConstant.ADD);
        return order;
    }

    /**
     * [计算价格]
     *
     * @param num
     * @param havePromoCode
     * @param price
     * @return java.math.BigDecimal
     * @author 陈湘岳 2025/8/30
     **/
    @Override
    public BigDecimal calculatePrice(int num, Boolean havePromoCode, BigDecimal price) {
        BigDecimal total = new BigDecimal(0);
        //如果价格为空或小于0，则返回0
        if (price== null||price.compareTo(new BigDecimal(0)) < 0){
            return total;
        }
        total = price.multiply(new BigDecimal(num)).multiply(new BigDecimal("12"));
        //按年付款9折
        total = total.multiply(new BigDecimal("0.9"));
        if (havePromoCode){
            total = total.multiply(new BigDecimal("0.9"));
        }
        return total;
    }
}
