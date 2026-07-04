package com.ruoyi.system.service;

import com.ruoyi.system.domain.vo.DashboardStatsVo;
import com.ruoyi.system.http.Result;

/**
 * 导览页统计服务
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-04-06
 */
public interface IDashboardService {

    /**
     * [查询导览页15个卡片统计数据]
     * 使用多线程并行查询，提升响应速度
     *
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/4/6
     **/
    Result getStats();

    /**
     * [查询销售图表数据]
     * @param type day=最近30天按日, month=最近12个月按月
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/4/6
     **/
    Result getSalesChart(String type);

    /**
     * [查询近30天品类销量排行Top5]
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/4/26
     **/
    Result getCategorySalesRank();

    /**
     * [查询近30天商品销量排行]
     * @param categoryId 品类ID，传null查全部
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/4/26
     **/
    Result getCommoditySalesRank(String categoryId);
}
