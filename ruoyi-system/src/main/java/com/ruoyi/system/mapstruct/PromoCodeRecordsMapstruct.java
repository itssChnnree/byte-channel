package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.entity.PromoCodeRecords;
import com.ruoyi.system.domain.dto.PromoCodeRecordsDto;
import com.ruoyi.system.domain.vo.PromoCodeRecordsVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 推广码记录表(PromoCodeRecords)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:13
 */
@Mapper(componentModel = "spring")
public interface PromoCodeRecordsMapstruct {
    PromoCodeRecordsMapstruct INSTANCE = Mappers.getMapper(PromoCodeRecordsMapstruct.class);
    
    /**
     * EntityתDTO
     *
     * @param promoCodeRecords
     * @return PromoCodeRecordsDTO
     * @author chenxiangyue 2025-07-20 23:24:13
     **/
    PromoCodeRecordsDto change2Dto(PromoCodeRecords promoCodeRecords);
    
    /**
     * DTO ת Entity
     *
     * @param promoCodeRecordsDto
     * @return PromoCodeRecords
     * @author chenxiangyue 2025-07-20 23:24:13
     **/
    PromoCodeRecords changeDto2(PromoCodeRecordsDto promoCodeRecordsDto);
    
    /**
     * DTO ת VO
     *
     * @param promoCodeRecordsDto
     * @return PromoCodeRecordsVO
     * @author chenxiangyue 2025-07-20 23:24:13
     **/
    PromoCodeRecordsVo changeDto2Vo(PromoCodeRecordsDto promoCodeRecordsDto);
    
    /**
     * VO ת DTO
     *
     * @param promoCodeRecordsVo
     * @return PromoCodeRecordsDTO
     * @author chenxiangyue 2025-07-20 23:24:13
     **/
    PromoCodeRecordsDto changeVo2Dto(PromoCodeRecordsVo promoCodeRecordsVo);
    
    /**
     * Entity ת VO
     *
     * @param promoCodeRecords
     * @return PromoCodeRecordsVO
     * @author chenxiangyue 2025-07-20 23:24:13
     **/
    PromoCodeRecordsVo change2Vo(PromoCodeRecords promoCodeRecords);
    
}
