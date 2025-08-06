package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.entity.OrderCommodityResources;
import com.ruoyi.system.domain.dto.OrderCommodityResourcesDto;
import com.ruoyi.system.domain.vo.OrderCommodityResourcesVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


/**
 * 订单商品资源(OrderCommodityResources)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:07
 */
@Mapper(componentModel = "spring")
public interface OrderCommodityResourcesMapstruct {
    OrderCommodityResourcesMapstruct INSTANCE = Mappers.getMapper(OrderCommodityResourcesMapstruct.class);
    
    /**
     * EntityתDTO
     *
     * @param orderCommodityResources
     * @return OrderCommodityResourcesDTO
     * @author chenxiangyue 2025-07-20 23:24:07
     **/
    OrderCommodityResourcesDto change2Dto(OrderCommodityResources orderCommodityResources);
    
    /**
     * DTO ת Entity
     *
     * @param orderCommodityResourcesDto
     * @return OrderCommodityResources
     * @author chenxiangyue 2025-07-20 23:24:07
     **/
    OrderCommodityResources changeDto2(OrderCommodityResourcesDto orderCommodityResourcesDto);
    
    /**
     * DTO ת VO
     *
     * @param orderCommodityResourcesDto
     * @return OrderCommodityResourcesVO
     * @author chenxiangyue 2025-07-20 23:24:07
     **/
    OrderCommodityResourcesVo changeDto2Vo(OrderCommodityResourcesDto orderCommodityResourcesDto);
    
    /**
     * VO ת DTO
     *
     * @param orderCommodityResourcesVo
     * @return OrderCommodityResourcesDTO
     * @author chenxiangyue 2025-07-20 23:24:07
     **/
    OrderCommodityResourcesDto changeVo2Dto(OrderCommodityResourcesVo orderCommodityResourcesVo);
    
    /**
     * Entity ת VO
     *
     * @param orderCommodityResources
     * @return OrderCommodityResourcesVO
     * @author chenxiangyue 2025-07-20 23:24:07
     **/
    OrderCommodityResourcesVo change2Vo(OrderCommodityResources orderCommodityResources);
    
}
