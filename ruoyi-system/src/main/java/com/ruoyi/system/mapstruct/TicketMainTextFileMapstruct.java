package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.dto.TicketMainTextFileDto;
import com.ruoyi.system.domain.vo.TicketMainTextFileVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.ruoyi.system.domain.entity.TicketMainTextFile;


/**
 * 工单正文文件附件表(TicketMainTextFile)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:37
 */
@Mapper(componentModel = "spring")
public interface TicketMainTextFileMapstruct {
    TicketMainTextFileMapstruct INSTANCE = Mappers.getMapper(TicketMainTextFileMapstruct.class);

    /**
     * EntityתDTO
     *
     * @param ticketMainTextFile
     * @return TicketMainTextFileDTO
     * @author chenxiangyue 2025-09-29 14:57:37
     **/
    TicketMainTextFileDto change2Dto(TicketMainTextFile ticketMainTextFile);

    /**
     * DTO ת Entity
     *
     * @param ticketMainTextFileDto
     * @return TicketMainTextFile
     * @author chenxiangyue 2025-09-29 14:57:37
     **/
    TicketMainTextFile changeDto2(TicketMainTextFileDto ticketMainTextFileDto);

    /**
     * DTO ת VO
     *
     * @param ticketMainTextFileDto
     * @return TicketMainTextFileVO
     * @author chenxiangyue 2025-09-29 14:57:37
     **/
    TicketMainTextFileVo changeDto2Vo(TicketMainTextFileDto ticketMainTextFileDto);

    /**
     * VO ת DTO
     *
     * @param ticketMainTextFileVo
     * @return TicketMainTextFileDTO
     * @author chenxiangyue 2025-09-29 14:57:37
     **/
    TicketMainTextFileDto changeVo2Dto(TicketMainTextFileVo ticketMainTextFileVo);

    /**
     * Entity ת VO
     *
     * @param ticketMainTextFile
     * @return TicketMainTextFileVO
     * @author chenxiangyue 2025-09-29 14:57:37
     **/
    TicketMainTextFileVo change2Vo(TicketMainTextFile ticketMainTextFile);

}
