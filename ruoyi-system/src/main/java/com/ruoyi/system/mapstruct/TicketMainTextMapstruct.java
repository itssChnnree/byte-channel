package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.dto.TicketMainTextDto;
import com.ruoyi.system.domain.vo.TicketMainTextVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.ruoyi.system.domain.entity.TicketMainText;


/**
 * 工单正文表(TicketMainText)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:36
 */
@Mapper(componentModel = "spring")
public interface TicketMainTextMapstruct {
    TicketMainTextMapstruct INSTANCE = Mappers.getMapper(TicketMainTextMapstruct.class);

    /**
     * EntityתDTO
     *
     * @param ticketMainText
     * @return TicketMainTextDTO
     * @author chenxiangyue 2025-09-29 14:57:36
     **/
    TicketMainTextDto change2Dto(TicketMainText ticketMainText);

    /**
     * DTO ת Entity
     *
     * @param ticketMainTextDto
     * @return TicketMainText
     * @author chenxiangyue 2025-09-29 14:57:36
     **/
    TicketMainText changeDto2(TicketMainTextDto ticketMainTextDto);

    /**
     * DTO ת VO
     *
     * @param ticketMainTextDto
     * @return TicketMainTextVO
     * @author chenxiangyue 2025-09-29 14:57:36
     **/
    TicketMainTextVo changeDto2Vo(TicketMainTextDto ticketMainTextDto);

    /**
     * VO ת DTO
     *
     * @param ticketMainTextVo
     * @return TicketMainTextDTO
     * @author chenxiangyue 2025-09-29 14:57:36
     **/
    TicketMainTextDto changeVo2Dto(TicketMainTextVo ticketMainTextVo);

    /**
     * Entity ת VO
     *
     * @param ticketMainText
     * @return TicketMainTextVO
     * @author chenxiangyue 2025-09-29 14:57:36
     **/
    TicketMainTextVo change2Vo(TicketMainText ticketMainText);

}
