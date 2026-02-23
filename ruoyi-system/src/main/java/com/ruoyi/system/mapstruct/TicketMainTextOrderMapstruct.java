package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.dto.TicketMainTextOrderDto;
import com.ruoyi.system.domain.entity.TicketMainTextOrder;
import com.ruoyi.system.domain.vo.TicketMainTextOrderVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


/**
 * 工单正文订单信息表(TicketMainTextOrder)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-02-23 17:52:30
 */
@Mapper(componentModel = "spring")
public interface TicketMainTextOrderMapstruct {
    TicketMainTextOrderMapstruct INSTANCE = Mappers.getMapper(TicketMainTextOrderMapstruct.class);

    /**
     * Entity to DTO
     *
     * @param ticketMainTextOrder
     * @return TicketMainTextOrderDTO
     * @author chenxiangyue 2026-02-23 17:52:30
     **/
    TicketMainTextOrderDto change2Dto(TicketMainTextOrder ticketMainTextOrder);

    /**
     * DTO to Entity
     *
     * @param ticketMainTextOrderDto
     * @return TicketMainTextOrder
     * @author chenxiangyue 2026-02-23 17:52:30
     **/
    TicketMainTextOrder changeDto2(TicketMainTextOrderDto ticketMainTextOrderDto);

    /**
     * DTO to VO
     *
     * @param ticketMainTextOrderDto
     * @return TicketMainTextOrderVO
     * @author chenxiangyue 2026-02-23 17:52:30
     **/
    TicketMainTextOrderVo changeDto2Vo(TicketMainTextOrderDto ticketMainTextOrderDto);

    /**
     * vo to dto
     *
     * @param ticketMainTextOrderVo
     * @return TicketMainTextOrderDTO
     * @author chenxiangyue 2026-02-23 17:52:30
     **/
    TicketMainTextOrderDto changeVo2Dto(TicketMainTextOrderVo ticketMainTextOrderVo);

    /**
     * Entity to VO
     *
     * @param ticketMainTextOrder
     * @return TicketMainTextOrderVO
     * @author chenxiangyue 2026-02-23 17:52:30
     **/
    TicketMainTextOrderVo change2Vo(TicketMainTextOrder ticketMainTextOrder);

}
