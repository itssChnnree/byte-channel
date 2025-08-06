package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.domain.dto.ServerResourcesDto;
import com.ruoyi.system.domain.vo.ServerResourcesVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


/**
 * 服务器资源表(ServerResources)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:25
 */
@Mapper(componentModel = "spring")
public interface ServerResourcesMapstruct {

    ServerResourcesMapstruct INSTANCE = Mappers.getMapper(ServerResourcesMapstruct.class);
    
    /**
     * EntityתDTO
     *
     * @param serverResources
     * @return ServerResourcesDTO
     * @author chenxiangyue 2025-07-20 23:24:25
     **/
    ServerResourcesDto change2Dto(ServerResources serverResources);
    
    /**
     * DTO ת Entity
     *
     * @param serverResourcesDto
     * @return ServerResources
     * @author chenxiangyue 2025-07-20 23:24:25
     **/
    ServerResources changeDto2(ServerResourcesDto serverResourcesDto);
    
    /**
     * DTO ת VO
     *
     * @param serverResourcesDto
     * @return ServerResourcesVO
     * @author chenxiangyue 2025-07-20 23:24:25
     **/
    ServerResourcesVo changeDto2Vo(ServerResourcesDto serverResourcesDto);
    
    /**
     * VO ת DTO
     *
     * @param serverResourcesVo
     * @return ServerResourcesDTO
     * @author chenxiangyue 2025-07-20 23:24:25
     **/
    ServerResourcesDto changeVo2Dto(ServerResourcesVo serverResourcesVo);
    
    /**
     * Entity ת VO
     *
     * @param serverResources
     * @return ServerResourcesVO
     * @author chenxiangyue 2025-07-20 23:24:25
     **/
    ServerResourcesVo change2Vo(ServerResources serverResources);
    
}
