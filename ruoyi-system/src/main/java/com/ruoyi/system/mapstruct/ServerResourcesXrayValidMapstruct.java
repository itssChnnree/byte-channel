package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.dto.ServerResourcesXrayValidDto;
import com.ruoyi.system.domain.vo.ServerResourcesXrayValidVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.ruoyi.system.domain.entity.ServerResourcesXrayValid;


/**
 * 资源状态检查节点分布(ServerResourcesXrayValid)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-05 17:32:30
 */
@Mapper
public interface ServerResourcesXrayValidMapstruct {
    ServerResourcesXrayValidMapstruct INSTANCE = Mappers.getMapper(ServerResourcesXrayValidMapstruct.class);
    
    /**
     * EntityתDTO
     *
     * @param serverResourcesXrayValid
     * @return ServerResourcesXrayValidDTO
     * @author chenxiangyue 2025-08-05 17:32:30
     **/
    ServerResourcesXrayValidDto change2Dto(ServerResourcesXrayValid serverResourcesXrayValid);
    
    /**
     * DTO ת Entity
     *
     * @param serverResourcesXrayValidDto
     * @return ServerResourcesXrayValid
     * @author chenxiangyue 2025-08-05 17:32:30
     **/
    ServerResourcesXrayValid changeDto2(ServerResourcesXrayValidDto serverResourcesXrayValidDto);
    
    /**
     * DTO ת VO
     *
     * @param serverResourcesXrayValidDto
     * @return ServerResourcesXrayValidVO
     * @author chenxiangyue 2025-08-05 17:32:30
     **/
    ServerResourcesXrayValidVo changeDto2Vo(ServerResourcesXrayValidDto serverResourcesXrayValidDto);
    
    /**
     * VO ת DTO
     *
     * @param serverResourcesXrayValidVo
     * @return ServerResourcesXrayValidDTO
     * @author chenxiangyue 2025-08-05 17:32:30
     **/
    ServerResourcesXrayValidDto changeVo2Dto(ServerResourcesXrayValidVo serverResourcesXrayValidVo);
    
    /**
     * Entity ת VO
     *
     * @param serverResourcesXrayValid
     * @return ServerResourcesXrayValidVO
     * @author chenxiangyue 2025-08-05 17:32:30
     **/
    ServerResourcesXrayValidVo change2Vo(ServerResourcesXrayValid serverResourcesXrayValid);
    
}
