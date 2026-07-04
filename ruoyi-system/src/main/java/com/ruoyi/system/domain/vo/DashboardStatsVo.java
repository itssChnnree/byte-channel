package com.ruoyi.system.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 导览页统计数据
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-04-06
 */
public class DashboardStatsVo implements Serializable {

    /** 当日订单数 */
    private Long dailyOrders;

    /** 当日订单金额 */
    private BigDecimal dailyRevenue;

    /** 当月订单数 */
    private Long monthlyOrders;

    /** 当月订单金额 */
    private BigDecimal monthlyRevenue;

    /** 待处理工单 */
    private Long pendingTickets;

    /** 资源短缺订单 */
    private Long resourceShortageOrders;

    /** 全部商品分类数量 */
    private Long totalCategories;

    /** 全部商品数量 */
    private Long totalProducts;

    /** 上架商品分类数量 */
    private Long listedCategories;

    /** 上架商品数量 */
    private Long listedProducts;

    /** 厂商数量 */
    private Long vendorCount;

    /** 账号数量 */
    private Long accountCount;

    /** 全部资源数量 */
    private Long totalResources;

    /** 已售出资源数量 */
    private Long soldResources;

    /** 待退款订单数量 */
    private Long pendingRefundOrders;

    public Long getDailyOrders() {
        return dailyOrders;
    }

    public void setDailyOrders(Long dailyOrders) {
        this.dailyOrders = dailyOrders;
    }

    public BigDecimal getDailyRevenue() {
        return dailyRevenue;
    }

    public void setDailyRevenue(BigDecimal dailyRevenue) {
        this.dailyRevenue = dailyRevenue;
    }

    public Long getMonthlyOrders() {
        return monthlyOrders;
    }

    public void setMonthlyOrders(Long monthlyOrders) {
        this.monthlyOrders = monthlyOrders;
    }

    public BigDecimal getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(BigDecimal monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public Long getPendingTickets() {
        return pendingTickets;
    }

    public void setPendingTickets(Long pendingTickets) {
        this.pendingTickets = pendingTickets;
    }

    public Long getResourceShortageOrders() {
        return resourceShortageOrders;
    }

    public void setResourceShortageOrders(Long resourceShortageOrders) {
        this.resourceShortageOrders = resourceShortageOrders;
    }

    public Long getTotalCategories() {
        return totalCategories;
    }

    public void setTotalCategories(Long totalCategories) {
        this.totalCategories = totalCategories;
    }

    public Long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public Long getListedCategories() {
        return listedCategories;
    }

    public void setListedCategories(Long listedCategories) {
        this.listedCategories = listedCategories;
    }

    public Long getListedProducts() {
        return listedProducts;
    }

    public void setListedProducts(Long listedProducts) {
        this.listedProducts = listedProducts;
    }

    public Long getVendorCount() {
        return vendorCount;
    }

    public void setVendorCount(Long vendorCount) {
        this.vendorCount = vendorCount;
    }

    public Long getAccountCount() {
        return accountCount;
    }

    public void setAccountCount(Long accountCount) {
        this.accountCount = accountCount;
    }

    public Long getTotalResources() {
        return totalResources;
    }

    public void setTotalResources(Long totalResources) {
        this.totalResources = totalResources;
    }

    public Long getSoldResources() {
        return soldResources;
    }

    public void setSoldResources(Long soldResources) {
        this.soldResources = soldResources;
    }

    public Long getPendingRefundOrders() {
        return pendingRefundOrders;
    }

    public void setPendingRefundOrders(Long pendingRefundOrders) {
        this.pendingRefundOrders = pendingRefundOrders;
    }
}
