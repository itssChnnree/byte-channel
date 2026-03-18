package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import com.ruoyi.system.domain.entity.ProfitFlow;
import com.ruoyi.system.domain.dto.ProfitFlowDto;
import com.ruoyi.system.domain.vo.ProfitFlowVo;

/**
 * 利润流水表(ProfitFlow)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-15 00:12:36
 */
@Mapper(componentModel = "spring")
public interface ProfitFlowMapstruct {
    ProfitFlowMapstruct INSTANCE = Mappers.getMapper(ProfitFlowMapstruct.class);

    /**
     * Entity to DTO
     *
     * @param profitFlow
     * @return ProfitFlowDTO
     * @author chenxiangyue 2026-03-15 00:12:36
     **/
    ProfitFlowDto change2Dto(ProfitFlow profitFlow);

    /**
     * DTO to Entity
     *
     * @param profitFlowDto
     * @return ProfitFlow
     * @author chenxiangyue 2026-03-15 00:12:36
     **/
    ProfitFlow changeDto2(ProfitFlowDto profitFlowDto);

    /**
     * DTO to VO
     *
     * @param profitFlowDto
     * @return ProfitFlowVO
     * @author chenxiangyue 2026-03-15 00:12:36
     **/
    ProfitFlowVo changeDto2Vo(ProfitFlowDto profitFlowDto);

    /**
     * vo to dto
     *
     * @param profitFlowVo
     * @return ProfitFlowDTO
     * @author chenxiangyue 2026-03-15 00:12:36
     **/
    ProfitFlowDto changeVo2Dto(ProfitFlowVo profitFlowVo);

    /**
     * Entity to VO
     *
     * @param profitFlow
     * @return ProfitFlowVO
     * @author chenxiangyue 2026-03-15 00:12:36
     **/
    ProfitFlowVo change2Vo(ProfitFlow profitFlow);

}
