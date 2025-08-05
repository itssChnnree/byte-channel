package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.CommodityCategory;
import com.ruoyi.system.domain.dto.CommodityCategoryDto;
import com.ruoyi.system.domain.vo.CommodityCategoryVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;



/**
 * 商品类别(CommodityCategory)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:46
 */
@Mapper(componentModel = "spring")
public interface CommodityCategoryMapstruct {
    CommodityCategoryMapstruct INSTANCE = Mappers.getMapper(CommodityCategoryMapstruct.class);
    
    /**
     * EntityתDTO
     *
     * @param commodityCategory
     * @return CommodityCategoryDTO
     * @author chenxiangyue 2025-07-20 23:23:46
     **/
    CommodityCategoryDto change2Dto(CommodityCategory commodityCategory);
    
    /**
     * DTO ת Entity
     *
     * @param commodityCategoryDto
     * @return CommodityCategory
     * @author chenxiangyue 2025-07-20 23:23:46
     **/
    CommodityCategory changeDto2(CommodityCategoryDto commodityCategoryDto);
    
    /**
     * DTO ת VO
     *
     * @param commodityCategoryDto
     * @return CommodityCategoryVO
     * @author chenxiangyue 2025-07-20 23:23:46
     **/
    CommodityCategoryVo changeDto2Vo(CommodityCategoryDto commodityCategoryDto);
    
    /**
     * VO ת DTO
     *
     * @param commodityCategoryVo
     * @return CommodityCategoryDTO
     * @author chenxiangyue 2025-07-20 23:23:46
     **/
    CommodityCategoryDto changeVo2Dto(CommodityCategoryVo commodityCategoryVo);
    
    /**
     * Entity ת VO
     *
     * @param commodityCategory
     * @return CommodityCategoryVO
     * @author chenxiangyue 2025-07-20 23:23:46
     **/
    CommodityCategoryVo change2Vo(CommodityCategory commodityCategory);
    
}
