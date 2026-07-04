package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.RelayNodeDto;
import com.ruoyi.system.http.Result;

/**
 * 中转节点表(RelayNode)服务接口
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10
 */
public interface IRelayNodeService {

    /**
     * 新增中转节点
     *
     * @param dto 节点信息
     * @return 操作结果
     */
    Result insert(RelayNodeDto dto);

    /**
     * 编辑中转节点
     *
     * @param dto 节点信息（含id）
     * @return 操作结果
     */
    Result update(RelayNodeDto dto);

    /**
     * 逻辑删除中转节点
     *
     * @param id 节点ID
     * @return 操作结果
     */
    Result delete(String id);

    /**
     * 查询节点详情
     *
     * @param id 节点ID
     * @return RelayNodePageVo
     */
    Result findById(String id);

    /**
     * 分页查询节点列表
     *
     * @param dto 查询参数（含pageNum/pageSize/nodeNameLike）
     * @return PageInfo&lt;RelayNodePageVo&gt;
     */
    Result page(RelayNodeDto dto);

    /**
     * 查询全部未删除节点（供下拉选择）
     *
     * @return List&lt;RelayNodePageVo&gt;
     */
    Result list();

    /**
     * 切换节点启用/停用状态
     * 当前启用则停用，当前停用则启用
     *
     * @param id 节点ID
     * @return 操作结果
     */
    Result toggleStatus(String id);
}
