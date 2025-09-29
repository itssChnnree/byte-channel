package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.dto.TicketDto;
import com.ruoyi.system.domain.vo.TicketVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.ruoyi.system.domain.entity.Ticket;


/**
 * 工单主表(Ticket)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:34
 */
@Mapper(componentModel = "spring")
public interface TicketMapstruct {
    TicketMapstruct INSTANCE = Mappers.getMapper(TicketMapstruct.class);

    /**
     * EntityתDTO
     *
     * @param ticket
     * @return TicketDTO
     * @author chenxiangyue 2025-09-29 14:57:34
     **/
    TicketDto change2Dto(Ticket ticket);

    /**
     * DTO ת Entity
     *
     * @param ticketDto
     * @return Ticket
     * @author chenxiangyue 2025-09-29 14:57:34
     **/
    Ticket changeDto2(TicketDto ticketDto);

    /**
     * DTO ת VO
     *
     * @param ticketDto
     * @return TicketVO
     * @author chenxiangyue 2025-09-29 14:57:34
     **/
    TicketVo changeDto2Vo(TicketDto ticketDto);

    /**
     * VO ת DTO
     *
     * @param ticketVo
     * @return TicketDTO
     * @author chenxiangyue 2025-09-29 14:57:34
     **/
    TicketDto changeVo2Dto(TicketVo ticketVo);

    /**
     * Entity ת VO
     *
     * @param ticket
     * @return TicketVO
     * @author chenxiangyue 2025-09-29 14:57:34
     **/
    TicketVo change2Vo(Ticket ticket);

}
