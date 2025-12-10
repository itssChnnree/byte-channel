package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import com.ruoyi.system.domain.entity.ResourceBlockDomain;
import com.ruoyi.system.domain.dto.ResourceBlockDomainDto;
import com.ruoyi.system.domain.vo.ResourceBlockDomainVo;

/**
 * 资源屏蔽域名表(ResourceBlockDomain)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 14:37:28
 */
@Mapper(componentModel = "spring")
public interface ResourceBlockDomainMapstruct {
    ResourceBlockDomainMapstruct INSTANCE = Mappers.getMapper(ResourceBlockDomainMapstruct.class);

    /**
     * Entity to DTO
     *
     * @param resourceBlockDomain
     * @return ResourceBlockDomainDTO
     * @author chenxiangyue 2025-12-08 14:37:28
     **/
    ResourceBlockDomainDto change2Dto(ResourceBlockDomain resourceBlockDomain);

    /**
     * DTO to Entity
     *
     * @param resourceBlockDomainDto
     * @return ResourceBlockDomain
     * @author chenxiangyue 2025-12-08 14:37:28
     **/
    ResourceBlockDomain changeDto2(ResourceBlockDomainDto resourceBlockDomainDto);

    /**
     * DTO to VO
     *
     * @param resourceBlockDomainDto
     * @return ResourceBlockDomainVO
     * @author chenxiangyue 2025-12-08 14:37:28
     **/
    ResourceBlockDomainVo changeDto2Vo(ResourceBlockDomainDto resourceBlockDomainDto);

    /**
     * vo to dto
     *
     * @param resourceBlockDomainVo
     * @return ResourceBlockDomainDTO
     * @author chenxiangyue 2025-12-08 14:37:28
     **/
    ResourceBlockDomainDto changeVo2Dto(ResourceBlockDomainVo resourceBlockDomainVo);

    /**
     * Entity to VO
     *
     * @param resourceBlockDomain
     * @return ResourceBlockDomainVO
     * @author chenxiangyue 2025-12-08 14:37:28
     **/
    ResourceBlockDomainVo change2Vo(ResourceBlockDomain resourceBlockDomain);

}
