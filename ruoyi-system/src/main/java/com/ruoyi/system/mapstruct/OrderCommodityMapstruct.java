package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.dto.OrderCommodityDto;
import com.ruoyi.system.domain.vo.OrderCommodityVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.ruoyi.system.domain.entity.OrderCommodity;

/**
 * 订单商品(OrderCommodity)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-15 10:01:36
 */
@Mapper(componentModel = "spring")
public interface OrderCommodityMapstruct {
    OrderCommodityMapstruct INSTANCE = Mappers.getMapper(OrderCommodityMapstruct.class);
    
    /**
     * EntityתDTO
     *
     * @param orderCommodity
     * @return OrderCommodityDTO
     * @author chenxiangyue 2025-08-15 10:01:36
     **/
    OrderCommodityDto change2Dto(OrderCommodity orderCommodity);
    
    /**
     * DTO ת Entity
     *
     * @param orderCommodityDto
     * @return OrderCommodity
     * @author chenxiangyue 2025-08-15 10:01:36
     **/
    OrderCommodity changeDto2(OrderCommodityDto orderCommodityDto);
    
    /**
     * DTO ת VO
     *
     * @param orderCommodityDto
     * @return OrderCommodityVO
     * @author chenxiangyue 2025-08-15 10:01:36
     **/
    OrderCommodityVo changeDto2Vo(OrderCommodityDto orderCommodityDto);
    
    /**
     * VO ת DTO
     *
     * @param orderCommodityVo
     * @return OrderCommodityDTO
     * @author chenxiangyue 2025-08-15 10:01:36
     **/
    OrderCommodityDto changeVo2Dto(OrderCommodityVo orderCommodityVo);
    
    /**
     * Entity ת VO
     *
     * @param orderCommodity
     * @return OrderCommodityVO
     * @author chenxiangyue 2025-08-15 10:01:36
     **/
    OrderCommodityVo change2Vo(OrderCommodity orderCommodity);
    
}
