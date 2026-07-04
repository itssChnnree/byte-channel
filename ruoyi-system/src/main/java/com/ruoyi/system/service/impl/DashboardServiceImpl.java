package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.vo.DashboardStatsVo;
import com.ruoyi.system.domain.vo.SalesChartVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.CommodityCategoryMapper;
import com.ruoyi.system.mapper.CommodityMapper;
import com.ruoyi.system.mapper.OrderMapper;
import com.ruoyi.system.mapper.ServerResourcesMapper;
import com.ruoyi.system.mapper.TicketMapper;
import com.ruoyi.system.mapper.VendorAccountInformationMapper;
import com.ruoyi.system.mapper.VendorInformationMapper;
import com.ruoyi.system.service.IDashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 导览页统计服务实现
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-04-06
 */
@Service("dashboardService")
public class DashboardServiceImpl implements IDashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);

    /** 多线程并行查询线程池 */
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(8);

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private CommodityMapper commodityMapper;

    @Resource
    private CommodityCategoryMapper commodityCategoryMapper;

    @Resource
    private ServerResourcesMapper serverResourcesMapper;

    @Resource
    private TicketMapper ticketMapper;

    @Resource
    private VendorInformationMapper vendorInformationMapper;

    @Resource
    private VendorAccountInformationMapper vendorAccountInformationMapper;

    @Override
    public Result getStats() {
        DashboardStatsVo stats = new DashboardStatsVo();
        try {
            // 1. 当日订单数
            CompletableFuture<Long> dailyOrdersFuture = CompletableFuture.supplyAsync(
                    () -> orderMapper.countTodayOrders(), EXECUTOR);

            // 2. 当日订单金额
            CompletableFuture<BigDecimal> dailyRevenueFuture = CompletableFuture.supplyAsync(
                    () -> orderMapper.sumTodayRevenue(), EXECUTOR);

            // 3. 当月订单数
            CompletableFuture<Long> monthlyOrdersFuture = CompletableFuture.supplyAsync(
                    () -> orderMapper.countMonthOrders(), EXECUTOR);

            // 4. 当月订单金额
            CompletableFuture<BigDecimal> monthlyRevenueFuture = CompletableFuture.supplyAsync(
                    () -> orderMapper.sumMonthRevenue(), EXECUTOR);

            // 5. 待处理工单（等待客服回复）
            CompletableFuture<Long> pendingTicketsFuture = CompletableFuture.supplyAsync(
                    () -> ticketMapper.getNeedServiceReply(), EXECUTOR);

            // 6. 资源短缺订单（待分配资源）
            CompletableFuture<Long> resourceShortageFuture = CompletableFuture.supplyAsync(
                    () -> orderMapper.countResourceShortageOrders(), EXECUTOR);

            // 7. 全部商品分类数量
            CompletableFuture<Long> totalCategoriesFuture = CompletableFuture.supplyAsync(
                    () -> commodityCategoryMapper.countTotal(), EXECUTOR);

            // 8. 全部商品数量
            CompletableFuture<Long> totalProductsFuture = CompletableFuture.supplyAsync(
                    () -> commodityMapper.countTotal(), EXECUTOR);

            // 9. 上架商品分类数量（去重）
            CompletableFuture<Long> listedCategoriesFuture = CompletableFuture.supplyAsync(
                    () -> commodityMapper.countListedCategories(), EXECUTOR);

            // 10. 上架商品数量
            CompletableFuture<Long> listedProductsFuture = CompletableFuture.supplyAsync(
                    () -> commodityMapper.countListed(), EXECUTOR);

            // 11. 厂商数量
            CompletableFuture<Long> vendorCountFuture = CompletableFuture.supplyAsync(
                    () -> vendorInformationMapper.countTotal(), EXECUTOR);

            // 12. 账号数量
            CompletableFuture<Long> accountCountFuture = CompletableFuture.supplyAsync(
                    () -> vendorAccountInformationMapper.countTotal(), EXECUTOR);

            // 13. 全部资源数量
            CompletableFuture<Long> totalResourcesFuture = CompletableFuture.supplyAsync(
                    () -> serverResourcesMapper.countTotal(), EXECUTOR);

            // 14. 已售出资源数量
            CompletableFuture<Long> soldResourcesFuture = CompletableFuture.supplyAsync(
                    () -> serverResourcesMapper.countSold(), EXECUTOR);

            // 15. 待退款订单数量
            CompletableFuture<Long> pendingRefundFuture = CompletableFuture.supplyAsync(
                    () -> orderMapper.countPendingRefundOrders(), EXECUTOR);

            // 等待所有查询完成（最多等待30秒）
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    dailyOrdersFuture, dailyRevenueFuture, monthlyOrdersFuture, monthlyRevenueFuture,
                    pendingTicketsFuture, resourceShortageFuture, totalCategoriesFuture, totalProductsFuture,
                    listedCategoriesFuture, listedProductsFuture, vendorCountFuture, accountCountFuture,
                    totalResourcesFuture, soldResourcesFuture, pendingRefundFuture
            );
            allFutures.get(30, TimeUnit.SECONDS);

            // 组装结果，null值兜底为0
            stats.setDailyOrders(nullToZero(dailyOrdersFuture.get()));
            stats.setDailyRevenue(nullToZero(dailyRevenueFuture.get()));
            stats.setMonthlyOrders(nullToZero(monthlyOrdersFuture.get()));
            stats.setMonthlyRevenue(nullToZero(monthlyRevenueFuture.get()));
            stats.setPendingTickets(nullToZero(pendingTicketsFuture.get()));
            stats.setResourceShortageOrders(nullToZero(resourceShortageFuture.get()));
            stats.setTotalCategories(nullToZero(totalCategoriesFuture.get()));
            stats.setTotalProducts(nullToZero(totalProductsFuture.get()));
            stats.setListedCategories(nullToZero(listedCategoriesFuture.get()));
            stats.setListedProducts(nullToZero(listedProductsFuture.get()));
            stats.setVendorCount(nullToZero(vendorCountFuture.get()));
            stats.setAccountCount(nullToZero(accountCountFuture.get()));
            stats.setTotalResources(nullToZero(totalResourcesFuture.get()));
            stats.setSoldResources(nullToZero(soldResourcesFuture.get()));
            stats.setPendingRefundOrders(nullToZero(pendingRefundFuture.get()));

            log.info("导览页统计数据查询完成");
            return Result.success(stats);
        } catch (Exception e) {
            log.error("导览页统计数据查询失败", e);
            return Result.fail("统计数据查询失败: " + e.getMessage());
        }
    }

    private Long nullToZero(Long value) {
        return value == null ? 0L : value;
    }

    private BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    @Override
    public Result getSalesChart(String type) {
        if ("day".equals(type)) {
            return Result.success(orderMapper.getDailySalesChart());
        } else if ("month".equals(type)) {
            return Result.success(orderMapper.getMonthlySalesChart());
        }
        return Result.fail("类型参数错误，仅支持 day 或 month");
    }

    @Override
    public Result getCategorySalesRank() {
        return Result.success(orderMapper.getCategorySalesRank());
    }

    @Override
    public Result getCommoditySalesRank(String categoryId) {
        return Result.success(orderMapper.getCommoditySalesRank(categoryId));
    }
}
