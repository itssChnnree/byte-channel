package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.dto.RelayResourceDto;
import com.ruoyi.system.domain.vo.RelayResourceVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.RelayResource;

import java.util.List;

/**
 * 中转资源表(RelayResource)数据访问层
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10
 */
@Mapper
@Repository
public interface RelayResourceMapper extends BaseMapper<RelayResource> {

    /**
     * 根据ID查询资源详情
     */
    RelayResourceVo queryById(@Param("id") String id);

    /**
     * 按条件查询资源列表
     */
    List<RelayResourceVo> queryList(RelayResourceDto dto);

    /**
     * 按条件查询资源列表（限制条数）
     */
    List<RelayResourceVo> queryListLimit(@Param("dto") RelayResourceDto dto, @Param("limit") int limit);

    /**
     * 分页查询资源列表（LEFT JOIN relay_node获取节点名称）
     */
    List<RelayResourceVo> queryPage(@Param("dto") RelayResourceDto dto);

    /**
     * 切换资源启用/停用状态（原子翻转，无需查询当前状态）
     */
    int toggleAvailableStatus(@Param("id") String id);
}
