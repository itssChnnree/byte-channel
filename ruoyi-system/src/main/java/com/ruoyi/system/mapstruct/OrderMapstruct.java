package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.dto.OrderDto;
import com.ruoyi.system.domain.vo.OrderVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 订单表(Order)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:02
 */
@Mapper(componentModel = "spring")
public interface OrderMapstruct {
    OrderMapstruct INSTANCE = Mappers.getMapper(OrderMapstruct.class);
    
    /**
     * EntityתDTO
     *
     * @param order
     * @return OrderDTO
     * @author chenxiangyue 2025-07-20 23:24:02
     **/
    OrderDto change2Dto(Order order);
    
    /**
     * DTO ת Entity
     *
     * @param orderDto
     * @return Order
     * @author chenxiangyue 2025-07-20 23:24:02
     **/
    Order changeDto2(OrderDto orderDto);
    
    /**
     * DTO ת VO
     *
     * @param orderDto
     * @return OrderVO
     * @author chenxiangyue 2025-07-20 23:24:02
     **/
    OrderVo changeDto2Vo(OrderDto orderDto);
    
    /**
     * VO ת DTO
     *
     * @param orderVo
     * @return OrderDTO
     * @author chenxiangyue 2025-07-20 23:24:02
     **/
    OrderDto changeVo2Dto(OrderVo orderVo);
    
    /**
     * Entity ת VO
     *
     * @param order
     * @return OrderVO
     * @author chenxiangyue 2025-07-20 23:24:02
     **/
    OrderVo change2Vo(Order order);
    
}
