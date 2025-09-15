package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.dto.ResourceAllocationTemporaryStorageDto;
import com.ruoyi.system.domain.vo.ResourceAllocationTemporaryStorageVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.ruoyi.system.domain.entity.ResourceAllocationTemporaryStorage;


/**
 * 资源暂存表(ResourceAllocationTemporaryStorage)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-11 15:28:58
 */
@Mapper(componentModel = "spring")
public interface ResourceAllocationTemporaryStorageMapstruct {
    ResourceAllocationTemporaryStorageMapstruct INSTANCE = Mappers.getMapper(ResourceAllocationTemporaryStorageMapstruct.class);

    /**
     * EntityתDTO
     *
     * @param resourceAllocationTemporaryStorage
     * @return ResourceAllocationTemporaryStorageDTO
     * @author chenxiangyue 2025-09-11 15:28:58
     **/
    ResourceAllocationTemporaryStorageDto change2Dto(ResourceAllocationTemporaryStorage resourceAllocationTemporaryStorage);

    /**
     * DTO ת Entity
     *
     * @param resourceAllocationTemporaryStorageDto
     * @return ResourceAllocationTemporaryStorage
     * @author chenxiangyue 2025-09-11 15:28:58
     **/
    ResourceAllocationTemporaryStorage changeDto2(ResourceAllocationTemporaryStorageDto resourceAllocationTemporaryStorageDto);

    /**
     * DTO ת VO
     *
     * @param resourceAllocationTemporaryStorageDto
     * @return ResourceAllocationTemporaryStorageVO
     * @author chenxiangyue 2025-09-11 15:28:58
     **/
    ResourceAllocationTemporaryStorageVo changeDto2Vo(ResourceAllocationTemporaryStorageDto resourceAllocationTemporaryStorageDto);

    /**
     * VO ת DTO
     *
     * @param resourceAllocationTemporaryStorageVo
     * @return ResourceAllocationTemporaryStorageDTO
     * @author chenxiangyue 2025-09-11 15:28:58
     **/
    ResourceAllocationTemporaryStorageDto changeVo2Dto(ResourceAllocationTemporaryStorageVo resourceAllocationTemporaryStorageVo);

    /**
     * Entity ת VO
     *
     * @param resourceAllocationTemporaryStorage
     * @return ResourceAllocationTemporaryStorageVO
     * @author chenxiangyue 2025-09-11 15:28:58
     **/
    ResourceAllocationTemporaryStorageVo change2Vo(ResourceAllocationTemporaryStorage resourceAllocationTemporaryStorage);

}
