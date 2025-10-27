package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.dto.TicketMainTextQuoteDto;
import com.ruoyi.system.domain.vo.TicketMainTextQuoteVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.ruoyi.system.domain.entity.TicketMainTextQuote;


/**
 * 工单正文报价表(TicketMainTextQuote)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-10-14 22:36:23
 */
@Mapper(componentModel = "spring")
public interface TicketMainTextQuoteMapstruct {
    TicketMainTextQuoteMapstruct INSTANCE = Mappers.getMapper(TicketMainTextQuoteMapstruct.class);

    /**
     * EntityתDTO
     *
     * @param ticketMainTextQuote
     * @return TicketMainTextQuoteDTO
     * @author chenxiangyue 2025-10-14 22:36:23
     **/
    TicketMainTextQuoteDto change2Dto(TicketMainTextQuote ticketMainTextQuote);

    /**
     * DTO ת Entity
     *
     * @param ticketMainTextQuoteDto
     * @return TicketMainTextQuote
     * @author chenxiangyue 2025-10-14 22:36:23
     **/
    TicketMainTextQuote changeDto2(TicketMainTextQuoteDto ticketMainTextQuoteDto);

    /**
     * DTO ת VO
     *
     * @param ticketMainTextQuoteDto
     * @return TicketMainTextQuoteVO
     * @author chenxiangyue 2025-10-14 22:36:23
     **/
    TicketMainTextQuoteVo changeDto2Vo(TicketMainTextQuoteDto ticketMainTextQuoteDto);

    /**
     * VO ת DTO
     *
     * @param ticketMainTextQuoteVo
     * @return TicketMainTextQuoteDTO
     * @author chenxiangyue 2025-10-14 22:36:23
     **/
    TicketMainTextQuoteDto changeVo2Dto(TicketMainTextQuoteVo ticketMainTextQuoteVo);

    /**
     * Entity ת VO
     *
     * @param ticketMainTextQuote
     * @return TicketMainTextQuoteVO
     * @author chenxiangyue 2025-10-14 22:36:23
     **/
    TicketMainTextQuoteVo change2Vo(TicketMainTextQuote ticketMainTextQuote);

}
