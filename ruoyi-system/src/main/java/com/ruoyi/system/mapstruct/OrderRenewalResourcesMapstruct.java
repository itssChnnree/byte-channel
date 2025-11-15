package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;
import com.ruoyi.system.domain.entity.OrderRenewalResources;
import com.ruoyi.system.domain.dto.OrderRenewalResourcesDto;
import com.ruoyi.system.domain.vo.OrderRenewalResourcesVo;

/**
 * 订单-续费资源表(OrderRenewalResources)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-16 00:01:14
 */
@Mapper(componentModel = "spring")
public interface OrderRenewalResourcesMapstruct {
    OrderRenewalResourcesMapstruct INSTANCE = Mappers.getMapper(OrderRenewalResourcesMapstruct.class);
    
    /**
     * EntityתDTO
     *
     * @param orderRenewalResources
     * @return OrderRenewalResourcesDTO
     * @author chenxiangyue 2025-11-16 00:01:14
     **/
    OrderRenewalResourcesDto change2Dto(OrderRenewalResources orderRenewalResources);
    
    /**
     * DTO ת Entity
     *
     * @param orderRenewalResourcesDto
     * @return OrderRenewalResources
     * @author chenxiangyue 2025-11-16 00:01:14
     **/
    OrderRenewalResources changeDto2(OrderRenewalResourcesDto orderRenewalResourcesDto);
    
    /**
     * DTO ת VO
     *
     * @param orderRenewalResourcesDto
     * @return OrderRenewalResourcesVO
     * @author chenxiangyue 2025-11-16 00:01:14
     **/
    OrderRenewalResourcesVo changeDto2Vo(OrderRenewalResourcesDto orderRenewalResourcesDto);
    
    /**
     * VO ת DTO
     *
     * @param orderRenewalResourcesVo
     * @return OrderRenewalResourcesDTO
     * @author chenxiangyue 2025-11-16 00:01:14
     **/
    OrderRenewalResourcesDto changeVo2Dto(OrderRenewalResourcesVo orderRenewalResourcesVo);
    
    /**
     * Entity ת VO
     *
     * @param orderRenewalResources
     * @return OrderRenewalResourcesVO
     * @author chenxiangyue 2025-11-16 00:01:14
     **/
    OrderRenewalResourcesVo change2Vo(OrderRenewalResources orderRenewalResources);
    
}
