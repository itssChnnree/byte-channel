package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import com.ruoyi.system.domain.entity.RelayNode;
import com.ruoyi.system.domain.dto.RelayNodeDto;
import com.ruoyi.system.domain.vo.RelayNodeVo;

/**
 * 中转节点表(RelayNode)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10 14:57:35
 */
@Mapper(componentModel = "spring")
public interface RelayNodeMapstruct {
    RelayNodeMapstruct INSTANCE = Mappers.getMapper(RelayNodeMapstruct.class);

    /**
     * Entity to DTO
     *
     * @param relayNode
     * @return RelayNodeDTO
     * @author chenxiangyue 2026-05-10 14:57:35
     **/
    RelayNodeDto change2Dto(RelayNode relayNode);

    /**
     * DTO to Entity
     *
     * @param relayNodeDto
     * @return RelayNode
     * @author chenxiangyue 2026-05-10 14:57:35
     **/
    RelayNode changeDto2(RelayNodeDto relayNodeDto);

    /**
     * DTO to VO
     *
     * @param relayNodeDto
     * @return RelayNodeVO
     * @author chenxiangyue 2026-05-10 14:57:35
     **/
    RelayNodeVo changeDto2Vo(RelayNodeDto relayNodeDto);

    /**
     * vo to dto
     *
     * @param relayNodeVo
     * @return RelayNodeDTO
     * @author chenxiangyue 2026-05-10 14:57:35
     **/
    RelayNodeDto changeVo2Dto(RelayNodeVo relayNodeVo);

    /**
     * Entity to VO
     *
     * @param relayNode
     * @return RelayNodeVO
     * @author chenxiangyue 2026-05-10 14:57:36
     **/
    RelayNodeVo change2Vo(RelayNode relayNode);

}
