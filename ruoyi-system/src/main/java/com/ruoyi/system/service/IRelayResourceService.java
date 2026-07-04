package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.RelayResourceDto;
import com.ruoyi.system.http.Result;

/**
 * 中转资源表(RelayResource)服务接口
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10
 */
public interface IRelayResourceService {

    /**
     * 新增中转资源
     *
     * @param dto 资源信息
     * @return 操作结果
     */
    Result insert(RelayResourceDto dto);

    /**
     * 编辑中转资源
     *
     * @param dto 资源信息（含id）
     * @return 操作结果
     */
    Result update(RelayResourceDto dto);

    /**
     * 逻辑删除中转资源
     *
     * @param id 资源ID
     * @return 操作结果
     */
    Result delete(String id);

    /**
     * 查询资源详情
     *
     * @param id 资源ID
     * @return RelayResourcePageVo
     */
    Result findById(String id);

    /**
     * 分页查询资源列表（含节点名称）
     *
     * @param dto 查询参数（含pageNum/pageSize/resourceIpLike/nodeId）
     * @return PageInfo&lt;RelayResourcePageVo&gt;
     */
    Result page(RelayResourceDto dto);

    /**
     * 查询全部未删除资源（供下拉选择）
     *
     * @return List&lt;RelayResourcePageVo&gt;
     */
    Result list();

    /**
     * 切换资源启用/停用状态
     * 当前启用则停用，当前停用则启用
     *
     * @param id 资源ID
     * @return 操作结果
     */
    Result toggleStatus(String id);
}
