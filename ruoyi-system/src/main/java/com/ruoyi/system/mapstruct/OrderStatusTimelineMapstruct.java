package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import com.ruoyi.system.domain.entity.OrderStatusTimeline;
import com.ruoyi.system.domain.dto.OrderStatusTimelineDto;
import com.ruoyi.system.domain.vo.OrderStatusTimelineVo;

/**
 * 订单状态时间线记录表（一行记录所有变更时间）(OrderStatusTimeline)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-19 21:20:36
 */
@Mapper(componentModel = "spring")
public interface OrderStatusTimelineMapstruct {
    OrderStatusTimelineMapstruct INSTANCE = Mappers.getMapper(OrderStatusTimelineMapstruct.class);

    /**
     * Entity to DTO
     *
     * @param orderStatusTimeline
     * @return OrderStatusTimelineDTO
     * @author chenxiangyue 2025-11-19 21:20:36
     **/
    OrderStatusTimelineDto change2Dto(OrderStatusTimeline orderStatusTimeline);

    /**
     * DTO to Entity
     *
     * @param orderStatusTimelineDto
     * @return OrderStatusTimeline
     * @author chenxiangyue 2025-11-19 21:20:36
     **/
    OrderStatusTimeline changeDto2(OrderStatusTimelineDto orderStatusTimelineDto);

    /**
     * DTO to VO
     *
     * @param orderStatusTimelineDto
     * @return OrderStatusTimelineVO
     * @author chenxiangyue 2025-11-19 21:20:36
     **/
    OrderStatusTimelineVo changeDto2Vo(OrderStatusTimelineDto orderStatusTimelineDto);

    /**
     * vo to dto
     *
     * @param orderStatusTimelineVo
     * @return OrderStatusTimelineDTO
     * @author chenxiangyue 2025-11-19 21:20:36
     **/
    OrderStatusTimelineDto changeVo2Dto(OrderStatusTimelineVo orderStatusTimelineVo);

    /**
     * Entity to VO
     *
     * @param orderStatusTimeline
     * @return OrderStatusTimelineVO
     * @author chenxiangyue 2025-11-19 21:20:36
     **/
    OrderStatusTimelineVo change2Vo(OrderStatusTimeline orderStatusTimeline);

}
