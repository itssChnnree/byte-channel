package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.RelayPortDto;
import com.ruoyi.system.http.Result;

/**
 * 中转端口表(RelayPort)服务接口
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10
 */
public interface IRelayPortService {

    /**
     * 新增中转端口配置
     *
     * @param dto 端口配置信息
     * @return 操作结果
     */
    Result insert(RelayPortDto dto);

    /**
     * 编辑中转端口配置
     *
     * @param dto 端口配置信息（含id）
     * @return 操作结果
     */
    Result update(RelayPortDto dto);

    /**
     * 逻辑删除中转端口
     *
     * @param id 端口ID
     * @return 操作结果
     */
    Result delete(String id);

    /**
     * 查询端口详情
     *
     * @param id 端口ID
     * @return RelayPortPageVo
     */
    Result findById(String id);

    /**
     * 分页查询端口列表（含节点名称、资源IP）
     *
     * @param dto 查询参数（含pageNum/pageSize/keyword/nodeId/resourceIp）
     * @return PageInfo&lt;RelayPortPageVo&gt;
     */
    Result page(RelayPortDto dto);
}
