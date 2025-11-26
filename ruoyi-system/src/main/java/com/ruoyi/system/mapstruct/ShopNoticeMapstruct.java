package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import com.ruoyi.system.domain.entity.ShopNotice;
import com.ruoyi.system.domain.dto.ShopNoticeDto;
import com.ruoyi.system.domain.vo.ShopNoticeVo;

/**
 * 商城公告表(ShopNotice)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-23 22:53:07
 */
@Mapper(componentModel = "spring")
public interface ShopNoticeMapstruct {
    ShopNoticeMapstruct INSTANCE = Mappers.getMapper(ShopNoticeMapstruct.class);

    /**
     * Entity to DTO
     *
     * @param shopNotice
     * @return ShopNoticeDTO
     * @author chenxiangyue 2025-11-23 22:53:07
     **/
    ShopNoticeDto change2Dto(ShopNotice shopNotice);

    /**
     * DTO to Entity
     *
     * @param shopNoticeDto
     * @return ShopNotice
     * @author chenxiangyue 2025-11-23 22:53:07
     **/
    ShopNotice changeDto2(ShopNoticeDto shopNoticeDto);

    /**
     * DTO to VO
     *
     * @param shopNoticeDto
     * @return ShopNoticeVO
     * @author chenxiangyue 2025-11-23 22:53:07
     **/
    ShopNoticeVo changeDto2Vo(ShopNoticeDto shopNoticeDto);

    /**
     * vo to dto
     *
     * @param shopNoticeVo
     * @return ShopNoticeDTO
     * @author chenxiangyue 2025-11-23 22:53:07
     **/
    ShopNoticeDto changeVo2Dto(ShopNoticeVo shopNoticeVo);

    /**
     * Entity to VO
     *
     * @param shopNotice
     * @return ShopNoticeVO
     * @author chenxiangyue 2025-11-23 22:53:07
     **/
    ShopNoticeVo change2Vo(ShopNotice shopNotice);

}
