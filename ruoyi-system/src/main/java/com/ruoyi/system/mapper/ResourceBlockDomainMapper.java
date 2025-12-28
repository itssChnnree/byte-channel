package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.dto.ResourceBlockDomainDto;
import com.ruoyi.system.domain.vo.ResourceBlockDomainVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.ResourceBlockDomain;

import java.util.List;

/**
 * 资源屏蔽域名表(ResourceBlockDomain
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 14:37:28
 */
@Mapper
@Repository
public interface ResourceBlockDomainMapper extends BaseMapper<ResourceBlockDomain> {


    /**
     * [查询状态为状态集合中的域名屏蔽]
     * @author 陈湘岳 2025/12/8
     * @param statusList 状态集合
     * @return java.util.List<com.ruoyi.system.domain.entity.ResourceBlockDomain>
     **/
    List<ResourceBlockDomain> findAllNormal(@Param("statusList") List<String> statusList);


    /**
     * [通过前缀及域名查询是否存在该屏蔽]
     * @author 陈湘岳 2025/12/9
     * @param prefixType 前缀
     * @param domain 域名
     * @return com.ruoyi.system.domain.entity.ResourceBlockDomain
     **/
    ResourceBlockDomain findByPrefixTypeAndDomain(@Param("prefixType")String prefixType, @Param("domain") String domain);


    /**
     * [根据条件分页查询频闭域名]
     * @author 陈湘岳 2025/12/11
     * @param resourceBlockDomainDto 查询状态
     * @return java.util.List<ResourceBlockDomainVo>
     **/
    List<ResourceBlockDomainVo> pageList(@Param("dto") ResourceBlockDomainDto resourceBlockDomainDto);


    /**
     * [更新状态为正常]
     * @author 陈湘岳 2025/12/11
     * @param sourceStatus 源状态
     * @param targetStatus 目标状态
     * @return int
     **/
    int updateStatusToNormal(@Param("sourceStatus") String sourceStatus,
                             @Param("targetStatus")String targetStatus);


    /**
     * [删除指定状态的数据]
     * @author 陈湘岳 2025/12/28
     * @param status 状态
     * @return int
     **/
    int deleteByStatus(@Param("status") String status);
}
