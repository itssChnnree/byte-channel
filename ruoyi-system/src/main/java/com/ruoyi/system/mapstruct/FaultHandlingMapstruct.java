package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.entity.FaultHandling;
import com.ruoyi.system.domain.dto.FaultHandlingDto;
import com.ruoyi.system.domain.vo.FaultHandlingVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


/**
 * 故障处理流程表(FaultHandling)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:53
 */
@Mapper(componentModel = "spring")
public interface FaultHandlingMapstruct {
    FaultHandlingMapstruct INSTANCE = Mappers.getMapper(FaultHandlingMapstruct.class);
    
    /**
     * EntityתDTO
     *
     * @param faultHandling
     * @return FaultHandlingDTO
     * @author chenxiangyue 2025-07-20 23:23:53
     **/
    FaultHandlingDto change2Dto(FaultHandling faultHandling);
    
    /**
     * DTO ת Entity
     *
     * @param faultHandlingDto
     * @return FaultHandling
     * @author chenxiangyue 2025-07-20 23:23:53
     **/
    FaultHandling changeDto2(FaultHandlingDto faultHandlingDto);
    
    /**
     * DTO ת VO
     *
     * @param faultHandlingDto
     * @return FaultHandlingVO
     * @author chenxiangyue 2025-07-20 23:23:53
     **/
    FaultHandlingVo changeDto2Vo(FaultHandlingDto faultHandlingDto);
    
    /**
     * VO ת DTO
     *
     * @param faultHandlingVo
     * @return FaultHandlingDTO
     * @author chenxiangyue 2025-07-20 23:23:53
     **/
    FaultHandlingDto changeVo2Dto(FaultHandlingVo faultHandlingVo);
    
    /**
     * Entity ת VO
     *
     * @param faultHandling
     * @return FaultHandlingVO
     * @author chenxiangyue 2025-07-20 23:23:53
     **/
    FaultHandlingVo change2Vo(FaultHandling faultHandling);
    
}
