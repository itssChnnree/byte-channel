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
     * [查询所有资源屏蔽域名]
     * @author 陈湘岳 2025/12/8
     * @param status 状态
     * @return java.util.List<com.ruoyi.system.domain.entity.ResourceBlockDomain>
     **/
    List<ResourceBlockDomain> findAllNormal(@Param("status") String status);


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
    List<ResourceBlockDomainVo> pageList(ResourceBlockDomainDto resourceBlockDomainDto);

}
