package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import com.ruoyi.system.domain.entity.ShopNoticeFile;
import com.ruoyi.system.domain.dto.ShopNoticeFileDto;
import com.ruoyi.system.domain.vo.ShopNoticeFileVo;

/**
 * 公告附件表(ShopNoticeFile)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-23 22:38:29
 */
@Mapper(componentModel = "spring")
public interface ShopNoticeFileMapstruct {
    ShopNoticeFileMapstruct INSTANCE = Mappers.getMapper(ShopNoticeFileMapstruct.class);

    /**
     * Entity to DTO
     *
     * @param shopNoticeFile
     * @return ShopNoticeFileDTO
     * @author chenxiangyue 2025-11-23 22:38:29
     **/
    ShopNoticeFileDto change2Dto(ShopNoticeFile shopNoticeFile);

    /**
     * DTO to Entity
     *
     * @param shopNoticeFileDto
     * @return ShopNoticeFile
     * @author chenxiangyue 2025-11-23 22:38:29
     **/
    ShopNoticeFile changeDto2(ShopNoticeFileDto shopNoticeFileDto);

    /**
     * DTO to VO
     *
     * @param shopNoticeFileDto
     * @return ShopNoticeFileVO
     * @author chenxiangyue 2025-11-23 22:38:29
     **/
    ShopNoticeFileVo changeDto2Vo(ShopNoticeFileDto shopNoticeFileDto);

    /**
     * vo to dto
     *
     * @param shopNoticeFileVo
     * @return ShopNoticeFileDTO
     * @author chenxiangyue 2025-11-23 22:38:29
     **/
    ShopNoticeFileDto changeVo2Dto(ShopNoticeFileVo shopNoticeFileVo);

    /**
     * Entity to VO
     *
     * @param shopNoticeFile
     * @return ShopNoticeFileVO
     * @author chenxiangyue 2025-11-23 22:38:29
     **/
    ShopNoticeFileVo change2Vo(ShopNoticeFile shopNoticeFile);

}
