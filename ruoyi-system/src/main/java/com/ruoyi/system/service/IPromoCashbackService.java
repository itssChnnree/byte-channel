package com.ruoyi.system.service;

/**
 * 推广返现服务接口
 *
 * @author ruoyi
 * @date 2026/4/7
 */
public interface IPromoCashbackService {

    /**
     * 执行推广返现定时任务
     * 查询待确认且订单完成超过24小时的推广记录，为推荐人增加返现金额
     *
     * @return int 成功返现的记录数量
     */
    int execute();
}
