package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.dto.ServerResourcesRenewalDto;
import com.ruoyi.system.domain.vo.ServerResourcesRenewalVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.ruoyi.system.domain.entity.ServerResourcesRenewal;


/**
 * 服务器资源表(ServerResourcesRenewal)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-10-28 10:48:26
 */
@Mapper
public interface ServerResourcesRenewalMapstruct {
    ServerResourcesRenewalMapstruct INSTANCE = Mappers.getMapper(ServerResourcesRenewalMapstruct.class);
    
    /**
     * EntityתDTO
     *
     * @param serverResourcesRenewal
     * @return ServerResourcesRenewalDTO
     * @author chenxiangyue 2025-10-28 10:48:26
     **/
    ServerResourcesRenewalDto change2Dto(ServerResourcesRenewal serverResourcesRenewal);
    
    /**
     * DTO ת Entity
     *
     * @param serverResourcesRenewalDto
     * @return ServerResourcesRenewal
     * @author chenxiangyue 2025-10-28 10:48:26
     **/
    ServerResourcesRenewal changeDto2(ServerResourcesRenewalDto serverResourcesRenewalDto);
    
    /**
     * DTO ת VO
     *
     * @param serverResourcesRenewalDto
     * @return ServerResourcesRenewalVO
     * @author chenxiangyue 2025-10-28 10:48:26
     **/
    ServerResourcesRenewalVo changeDto2Vo(ServerResourcesRenewalDto serverResourcesRenewalDto);
    
    /**
     * VO ת DTO
     *
     * @param serverResourcesRenewalVo
     * @return ServerResourcesRenewalDTO
     * @author chenxiangyue 2025-10-28 10:48:26
     **/
    ServerResourcesRenewalDto changeVo2Dto(ServerResourcesRenewalVo serverResourcesRenewalVo);
    
    /**
     * Entity ת VO
     *
     * @param serverResourcesRenewal
     * @return ServerResourcesRenewalVO
     * @author chenxiangyue 2025-10-28 10:48:26
     **/
    ServerResourcesRenewalVo change2Vo(ServerResourcesRenewal serverResourcesRenewal);
    
}
