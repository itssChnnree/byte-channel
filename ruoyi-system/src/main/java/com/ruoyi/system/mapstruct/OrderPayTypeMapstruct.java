package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import com.ruoyi.system.domain.entity.OrderPayType;
import com.ruoyi.system.domain.dto.OrderPayTypeDto;
import com.ruoyi.system.domain.vo.OrderPayTypeVo;

/**
 * 订单支付方式表(OrderPayType)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-23 21:16:06
 */
@Mapper(componentModel = "spring")
public interface OrderPayTypeMapstruct {
    OrderPayTypeMapstruct INSTANCE = Mappers.getMapper(OrderPayTypeMapstruct.class);

    /**
     * Entity to DTO
     *
     * @param orderPayType
     * @return OrderPayTypeDTO
     * @author chenxiangyue 2026-03-23 21:16:06
     **/
    OrderPayTypeDto change2Dto(OrderPayType orderPayType);

    /**
     * DTO to Entity
     *
     * @param orderPayTypeDto
     * @return OrderPayType
     * @author chenxiangyue 2026-03-23 21:16:06
     **/
    OrderPayType changeDto2(OrderPayTypeDto orderPayTypeDto);

    /**
     * DTO to VO
     *
     * @param orderPayTypeDto
     * @return OrderPayTypeVO
     * @author chenxiangyue 2026-03-23 21:16:06
     **/
    OrderPayTypeVo changeDto2Vo(OrderPayTypeDto orderPayTypeDto);

    /**
     * vo to dto
     *
     * @param orderPayTypeVo
     * @return OrderPayTypeDTO
     * @author chenxiangyue 2026-03-23 21:16:06
     **/
    OrderPayTypeDto changeVo2Dto(OrderPayTypeVo orderPayTypeVo);

    /**
     * Entity to VO
     *
     * @param orderPayType
     * @return OrderPayTypeVO
     * @author chenxiangyue 2026-03-23 21:16:06
     **/
    OrderPayTypeVo change2Vo(OrderPayType orderPayType);

}
