package com.ruoyi.system.service;

/**
 * 资源分配服务接口
 *
 * @author ruoyi
 * @date 2026/3/23
 */
public interface IResourceAllocationService {

    /**
     * 执行资源分配定时任务
     * 查询所有待分配资源的新购订单，为每个订单分配资源
     *
     * @return int 成功分配资源的订单数量
     */
    int execute();
}
