package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import com.ruoyi.system.domain.entity.RelayResource;
import com.ruoyi.system.domain.dto.RelayResourceDto;
import com.ruoyi.system.domain.vo.RelayResourceVo;

/**
 * 中转资源表(RelayResource)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10 14:57:39
 */
@Mapper(componentModel = "spring")
public interface RelayResourceMapstruct {
    RelayResourceMapstruct INSTANCE = Mappers.getMapper(RelayResourceMapstruct.class);

    /**
     * Entity to DTO
     *
     * @param relayResource
     * @return RelayResourceDTO
     * @author chenxiangyue 2026-05-10 14:57:39
     **/
    RelayResourceDto change2Dto(RelayResource relayResource);

    /**
     * DTO to Entity
     *
     * @param relayResourceDto
     * @return RelayResource
     * @author chenxiangyue 2026-05-10 14:57:39
     **/
    RelayResource changeDto2(RelayResourceDto relayResourceDto);

    /**
     * DTO to VO
     *
     * @param relayResourceDto
     * @return RelayResourceVO
     * @author chenxiangyue 2026-05-10 14:57:39
     **/
    RelayResourceVo changeDto2Vo(RelayResourceDto relayResourceDto);

    /**
     * vo to dto
     *
     * @param relayResourceVo
     * @return RelayResourceDTO
     * @author chenxiangyue 2026-05-10 14:57:39
     **/
    RelayResourceDto changeVo2Dto(RelayResourceVo relayResourceVo);

    /**
     * Entity to VO
     *
     * @param relayResource
     * @return RelayResourceVO
     * @author chenxiangyue 2026-05-10 14:57:39
     **/
    RelayResourceVo change2Vo(RelayResource relayResource);

}
