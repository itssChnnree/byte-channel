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
import com.ruoyi.system.util.LogEsUtil;
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
    public Order createOrder(int num, Commodity normalCommodity, PromoCodeRecords promoCodeRecords) {
        LogEsUtil.info("按月创建订单");
        BigDecimal price = null;
        //存在折扣价则使用折扣价，不存在则使用原价
        if (ObjectUtil.isNotEmpty(normalCommodity.getCommodityDiscountedPrice())){
            price = normalCommodity.getCommodityDiscountedPrice();
        }else {
            price = normalCommodity.getCommodityPrice();
        }
        //计算价格
        BigDecimal total = calculatePrice(num, ObjectUtil.isNotEmpty(promoCodeRecords), price);

        Order order = new Order();
        order.setUserId(SecurityUtils.getStrUserId());
        order.setAmount(total);
        order.setStatus(OrderStatus.WAIT_PAY);
        order.setOrderTime(new Date());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setDescription("按月订购节点");
        order.setPaymentPeriod(PaymentPeriodConstant.MONTHLY);
        order.setOrderType(OrderTypeConstant.ADD);
        order.setIsDeleted(0);
        return order;
    }

    @Override
    public Order createRenewalOrder(PromoCodeRecords promoCodeRecords, Commodity normalCommodity) {
        BigDecimal price = null;
        //存在折扣价则使用折扣价，不存在则使用原价
        if (ObjectUtil.isNotEmpty(normalCommodity.getCommodityDiscountedPrice())){
            price = normalCommodity.getCommodityDiscountedPrice();
        }else {
            price = normalCommodity.getCommodityPrice();
        }
        //计算价格
        BigDecimal total = calculatePrice(1, ObjectUtil.isNotEmpty(promoCodeRecords), price);

        Order order = new Order();
        order.setUserId(SecurityUtils.getStrUserId());
        order.setAmount(total);
        order.setStatus(OrderStatus.WAIT_PAY);
        order.setOrderTime(new Date());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setDescription("按月续费节点");
        order.setPaymentPeriod(PaymentPeriodConstant.MONTHLY);
        order.setOrderType(OrderTypeConstant.RENEW);
        order.setIsDeleted(0);
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
        total = price.multiply(new BigDecimal(num));
        if (havePromoCode){
            total = total.multiply(new BigDecimal("0.9"));
        }
        return total;
    }
}
