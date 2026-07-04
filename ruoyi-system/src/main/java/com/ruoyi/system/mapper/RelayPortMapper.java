package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.dto.RelayPortDto;
import com.ruoyi.system.domain.vo.RelayPortVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.RelayPort;

import java.util.List;

/**
 * 中转端口表(RelayPort)数据访问层
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10
 */
@Mapper
@Repository
public interface RelayPortMapper extends BaseMapper<RelayPort> {

    /**
     * 根据ID查询端口详情
     */
    RelayPortVo queryById(@Param("id") String id);

    /**
     * 按条件查询端口列表
     */
    List<RelayPortVo> queryList(RelayPortDto dto);

    /**
     * 按条件查询端口列表（限制条数）
     */
    List<RelayPortVo> queryListLimit(@Param("dto") RelayPortDto dto, @Param("limit") int limit);

    /**
     * 分页查询端口列表（LEFT JOIN relay_resource + relay_node获取关联信息）
     */
    List<RelayPortVo> queryPage(@Param("dto") RelayPortDto dto);
}
