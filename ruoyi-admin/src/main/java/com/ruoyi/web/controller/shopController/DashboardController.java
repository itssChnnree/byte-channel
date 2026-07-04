package com.ruoyi.web.controller.shopController;

import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IDashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

/**
 * 导览页统计数据
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-04-06
 */
@Api(tags = "导览页统计数据")
@RestController
@RequestMapping("dashboard")
public class DashboardController {

    @Resource(name = "dashboardService")
    private IDashboardService dashboardService;

    @ApiOperation("获取导览页15个卡片统计数据（多线程并行查询）")
    @GetMapping("/getStats")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result getStats() {
        return dashboardService.getStats();
    }

    @ApiOperation("获取销售图表数据")
    @GetMapping("/getSalesChart")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result getSalesChart(String type) {
        return dashboardService.getSalesChart(type);
    }

    @ApiOperation("获取近30天品类销量排行Top5")
    @GetMapping("/getCategorySalesRank")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result getCategorySalesRank() {
        return dashboardService.getCategorySalesRank();
    }

    @ApiOperation("获取近30天商品销量排行")
    @GetMapping("/getCommoditySalesRank")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result getCommoditySalesRank(String categoryId) {
        return dashboardService.getCommoditySalesRank(categoryId);
    }
}
