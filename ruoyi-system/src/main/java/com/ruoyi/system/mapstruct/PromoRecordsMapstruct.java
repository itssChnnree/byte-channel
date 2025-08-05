package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.PromoRecords;
import com.ruoyi.system.domain.dto.PromoRecordsDto;
import com.ruoyi.system.domain.vo.PromoRecordsVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;


/**
 * 推广记录表(PromoRecords)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:19
 */
@Mapper(componentModel = "spring")
public interface PromoRecordsMapstruct {
    PromoRecordsMapstruct INSTANCE = Mappers.getMapper(PromoRecordsMapstruct.class);
    
    /**
     * EntityתDTO
     *
     * @param promoRecords
     * @return PromoRecordsDTO
     * @author chenxiangyue 2025-07-20 23:24:19
     **/
    PromoRecordsDto change2Dto(PromoRecords promoRecords);
    
    /**
     * DTO ת Entity
     *
     * @param promoRecordsDto
     * @return PromoRecords
     * @author chenxiangyue 2025-07-20 23:24:19
     **/
    PromoRecords changeDto2(PromoRecordsDto promoRecordsDto);
    
    /**
     * DTO ת VO
     *
     * @param promoRecordsDto
     * @return PromoRecordsVO
     * @author chenxiangyue 2025-07-20 23:24:19
     **/
    PromoRecordsVo changeDto2Vo(PromoRecordsDto promoRecordsDto);
    
    /**
     * VO ת DTO
     *
     * @param promoRecordsVo
     * @return PromoRecordsDTO
     * @author chenxiangyue 2025-07-20 23:24:19
     **/
    PromoRecordsDto changeVo2Dto(PromoRecordsVo promoRecordsVo);
    
    /**
     * Entity ת VO
     *
     * @param promoRecords
     * @return PromoRecordsVO
     * @author chenxiangyue 2025-07-20 23:24:19
     **/
    PromoRecordsVo change2Vo(PromoRecords promoRecords);
    
}
