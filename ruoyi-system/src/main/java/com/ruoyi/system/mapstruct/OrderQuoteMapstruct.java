package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import com.ruoyi.system.domain.entity.OrderQuote;
import com.ruoyi.system.domain.dto.OrderQuoteDto;
import com.ruoyi.system.domain.vo.OrderQuoteVo;

/**
 * 订单报价表(OrderQuote)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-02-27 00:11:14
 */
@Mapper(componentModel = "spring")
public interface OrderQuoteMapstruct {
    OrderQuoteMapstruct INSTANCE = Mappers.getMapper(OrderQuoteMapstruct.class);

    /**
     * Entity to DTO
     *
     * @param orderQuote
     * @return OrderQuoteDTO
     * @author chenxiangyue 2026-02-27 00:11:14
     **/
    OrderQuoteDto change2Dto(OrderQuote orderQuote);

    /**
     * DTO to Entity
     *
     * @param orderQuoteDto
     * @return OrderQuote
     * @author chenxiangyue 2026-02-27 00:11:14
     **/
    OrderQuote changeDto2(OrderQuoteDto orderQuoteDto);

    /**
     * DTO to VO
     *
     * @param orderQuoteDto
     * @return OrderQuoteVO
     * @author chenxiangyue 2026-02-27 00:11:14
     **/
    OrderQuoteVo changeDto2Vo(OrderQuoteDto orderQuoteDto);

    /**
     * vo to dto
     *
     * @param orderQuoteVo
     * @return OrderQuoteDTO
     * @author chenxiangyue 2026-02-27 00:11:14
     **/
    OrderQuoteDto changeVo2Dto(OrderQuoteVo orderQuoteVo);

    /**
     * Entity to VO
     *
     * @param orderQuote
     * @return OrderQuoteVO
     * @author chenxiangyue 2026-02-27 00:11:14
     **/
    OrderQuoteVo change2Vo(OrderQuote orderQuote);

}
