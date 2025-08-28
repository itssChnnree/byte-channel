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
import com.ruoyi.system.mapper.PromoCodeRecordsMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/12
 */

@Component(PaymentPeriodConstant.MONTHLY)
public class MonthlyOrderCreateStrategy implements OrderCreateStrategy{


    @Override
    public Order createOrder(OrderByCommodityDto orderByCommodityDto, Commodity normalCommodity, PromoCodeRecords promoCodeRecords) {
        BigDecimal total = new BigDecimal(0);
        //存在折扣价则使用折扣价，不存在则使用原价
        if (ObjectUtil.isNotEmpty(normalCommodity.getCommodityDiscountedPrice())){
            total = normalCommodity.getCommodityDiscountedPrice().multiply(new BigDecimal(orderByCommodityDto.getNum()));
        }else {
            total = normalCommodity.getCommodityPrice().multiply(new BigDecimal(orderByCommodityDto.getNum()));
        }
        //存在优惠码则使用优惠码
        if (ObjectUtil.isNotEmpty(promoCodeRecords)){
            total = total.multiply(new BigDecimal("0.9"));
        }

        Order order = new Order();
        order.setUserId(SecurityUtils.getStrUserId());
        order.setAmount(total);
        order.setStatus(OrderStatus.WAIT_PAY);
        order.setOrderTime(new Date());
        order.setDescription("按月订购节点");
        order.setPaymentPeriod(PaymentPeriodConstant.MONTHLY);
        order.setOrderType(OrderTypeConstant.ADD);
        return order;
    }
}
