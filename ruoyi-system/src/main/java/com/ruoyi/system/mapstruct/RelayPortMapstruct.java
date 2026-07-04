package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import com.ruoyi.system.domain.entity.RelayPort;
import com.ruoyi.system.domain.dto.RelayPortDto;
import com.ruoyi.system.domain.vo.RelayPortVo;

/**
 * 中转端口表(RelayPort)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10 14:57:37
 */
@Mapper(componentModel = "spring")
public interface RelayPortMapstruct {
    RelayPortMapstruct INSTANCE = Mappers.getMapper(RelayPortMapstruct.class);

    /**
     * Entity to DTO
     *
     * @param relayPort
     * @return RelayPortDTO
     * @author chenxiangyue 2026-05-10 14:57:37
     **/
    RelayPortDto change2Dto(RelayPort relayPort);

    /**
     * DTO to Entity
     *
     * @param relayPortDto
     * @return RelayPort
     * @author chenxiangyue 2026-05-10 14:57:37
     **/
    RelayPort changeDto2(RelayPortDto relayPortDto);

    /**
     * DTO to VO
     *
     * @param relayPortDto
     * @return RelayPortVO
     * @author chenxiangyue 2026-05-10 14:57:37
     **/
    RelayPortVo changeDto2Vo(RelayPortDto relayPortDto);

    /**
     * vo to dto
     *
     * @param relayPortVo
     * @return RelayPortDTO
     * @author chenxiangyue 2026-05-10 14:57:37
     **/
    RelayPortDto changeVo2Dto(RelayPortVo relayPortVo);

    /**
     * Entity to VO
     *
     * @param relayPort
     * @return RelayPortVO
     * @author chenxiangyue 2026-05-10 14:57:37
     **/
    RelayPortVo change2Vo(RelayPort relayPort);

}
