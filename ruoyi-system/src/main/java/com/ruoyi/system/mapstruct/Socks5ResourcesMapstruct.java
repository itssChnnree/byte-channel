package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import com.ruoyi.system.domain.entity.Socks5Resources;
import com.ruoyi.system.domain.dto.Socks5ResourcesDto;
import com.ruoyi.system.domain.vo.Socks5ResourcesVo;

/**
 * socks5资源记录(Socks5Resources)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-06-29 22:23:01
 */
@Mapper(componentModel = "spring")
public interface Socks5ResourcesMapstruct {
    Socks5ResourcesMapstruct INSTANCE = Mappers.getMapper(Socks5ResourcesMapstruct.class);

    /**
     * Entity to DTO
     *
     * @param socks5Resources
     * @return Socks5ResourcesDTO
     * @author chenxiangyue 2026-06-29 22:23:01
     **/
    Socks5ResourcesDto change2Dto(Socks5Resources socks5Resources);

    /**
     * DTO to Entity
     *
     * @param socks5ResourcesDto
     * @return Socks5Resources
     * @author chenxiangyue 2026-06-29 22:23:01
     **/
    Socks5Resources changeDto2(Socks5ResourcesDto socks5ResourcesDto);

    /**
     * DTO to VO
     *
     * @param socks5ResourcesDto
     * @return Socks5ResourcesVO
     * @author chenxiangyue 2026-06-29 22:23:01
     **/
    Socks5ResourcesVo changeDto2Vo(Socks5ResourcesDto socks5ResourcesDto);

    /**
     * vo to dto
     *
     * @param socks5ResourcesVo
     * @return Socks5ResourcesDTO
     * @author chenxiangyue 2026-06-29 22:23:01
     **/
    Socks5ResourcesDto changeVo2Dto(Socks5ResourcesVo socks5ResourcesVo);

    /**
     * Entity to VO
     *
     * @param socks5Resources
     * @return Socks5ResourcesVO
     * @author chenxiangyue 2026-06-29 22:23:01
     **/
    Socks5ResourcesVo change2Vo(Socks5Resources socks5Resources);

}
