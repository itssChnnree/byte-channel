package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.entity.Commodity;
import com.ruoyi.system.domain.dto.CommodityDto;
import com.ruoyi.system.domain.vo.CommodityVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


/**
 * 商品表(Commodity)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:33
 */
@Mapper(componentModel = "spring")
public interface CommodityMapstruct {
    CommodityMapstruct INSTANCE = Mappers.getMapper(CommodityMapstruct.class);
    
    /**
     * EntityתDTO
     *
     * @param commodity
     * @return CommodityDTO
     * @author chenxiangyue 2025-07-20 23:23:33
     **/
    CommodityDto change2Dto(Commodity commodity);
    
    /**
     * DTO ת Entity
     *
     * @param commodityDto
     * @return Commodity
     * @author chenxiangyue 2025-07-20 23:23:33
     **/
    Commodity changeDto2(CommodityDto commodityDto);
    
    /**
     * DTO ת VO
     *
     * @param commodityDto
     * @return CommodityVO
     * @author chenxiangyue 2025-07-20 23:23:33
     **/
    CommodityVo changeDto2Vo(CommodityDto commodityDto);
    
    /**
     * VO ת DTO
     *
     * @param commodityVo
     * @return CommodityDTO
     * @author chenxiangyue 2025-07-20 23:23:33
     **/
    CommodityDto changeVo2Dto(CommodityVo commodityVo);
    
    /**
     * Entity ת VO
     *
     * @param commodity
     * @return CommodityVO
     * @author chenxiangyue 2025-07-20 23:23:33
     **/
    CommodityVo change2Vo(Commodity commodity);
    
}
