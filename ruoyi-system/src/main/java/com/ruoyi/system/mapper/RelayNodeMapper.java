package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.dto.RelayNodeDto;
import com.ruoyi.system.domain.vo.RelayNodePageVo;
import com.ruoyi.system.domain.vo.RelayNodeVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.RelayNode;

import java.util.List;

/**
 * 中转节点表(RelayNode)数据访问层
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10
 */
@Mapper
@Repository
public interface RelayNodeMapper extends BaseMapper<RelayNode> {

    /**
     * 根据ID查询节点详情
     */
    RelayNodeVo queryById(@Param("id") String id);

    /**
     * 按条件查询节点列表
     */
    List<RelayNodeVo> queryList(RelayNodeDto dto);

    /**
     * 按条件查询节点列表（限制条数）
     */
    List<RelayNodeVo> queryListLimit(@Param("dto") RelayNodeDto dto, @Param("limit") int limit);

    /**
     * 分页查询节点列表
     */
    List<RelayNodeVo> queryPage(@Param("dto") RelayNodeDto dto);

    /**
     * 分页查询节点列表（直接返回RelayNodePageVo，含资源数量子查询）
     */
    List<RelayNodePageVo> queryPageVo(@Param("dto") RelayNodeDto dto);

    /**
     * 查询全部节点（返回RelayNodePageVo，含资源数量子查询）
     */
    List<RelayNodePageVo> queryListVo();

    /**
     * 切换节点启用/停用状态（原子翻转，无需查询当前状态）
     */
    int toggleAvailableStatus(@Param("id") String id);
}
