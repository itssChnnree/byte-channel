package com.ruoyi.system.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售图表数据点
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-04-06
 */
public class SalesChartVo implements Serializable {

    /** X轴标签（日期/月份） */
    private String name;

    /** 销售额 */
    private BigDecimal amount;

    /** 订单数量 */
    private Long orderCount;

    /** 续费订单数量 */
    private Long renewCount;

    /** 新购订单数量 */
    private Long addCount;

    /** 新购销售额 */
    private BigDecimal addAmount;

    /** 续费销售额 */
    private BigDecimal renewAmount;

    /** 充值订单数量 */
    private Long rechargeCount;

    /** 充值销售额 */
    private BigDecimal rechargeAmount;

    /** 报价订单数量 */
    private Long quoteCount;

    /** 报价销售额 */
    private BigDecimal quoteAmount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public Long getRenewCount() {
        return renewCount;
    }

    public void setRenewCount(Long renewCount) {
        this.renewCount = renewCount;
    }

    public Long getAddCount() {
        return addCount;
    }

    public void setAddCount(Long addCount) {
        this.addCount = addCount;
    }

    public BigDecimal getAddAmount() {
        return addAmount;
    }

    public void setAddAmount(BigDecimal addAmount) {
        this.addAmount = addAmount;
    }

    public BigDecimal getRenewAmount() {
        return renewAmount;
    }

    public void setRenewAmount(BigDecimal renewAmount) {
        this.renewAmount = renewAmount;
    }

    public Long getRechargeCount() {
        return rechargeCount;
    }

    public void setRechargeCount(Long rechargeCount) {
        this.rechargeCount = rechargeCount;
    }

    public BigDecimal getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(BigDecimal rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public Long getQuoteCount() {
        return quoteCount;
    }

    public void setQuoteCount(Long quoteCount) {
        this.quoteCount = quoteCount;
    }

    public BigDecimal getQuoteAmount() {
        return quoteAmount;
    }

    public void setQuoteAmount(BigDecimal quoteAmount) {
        this.quoteAmount = quoteAmount;
    }
}
