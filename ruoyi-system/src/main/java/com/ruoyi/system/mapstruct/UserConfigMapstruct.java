package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;
import com.ruoyi.system.domain.entity.UserConfig;
import com.ruoyi.system.domain.dto.UserConfigDto;
import com.ruoyi.system.domain.vo.UserConfigVo;

/**
 * 用户配置表(UserConfig)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-04-01 23:29:53
 */
@Mapper(componentModel = "spring")
public interface UserConfigMapstruct {
    UserConfigMapstruct INSTANCE = Mappers.getMapper(UserConfigMapstruct.class);
    
    /**
     * Entity to DTO
     *
     * @param userConfig
     * @return UserConfigDTO
     * @author chenxiangyue 2026-04-01 23:29:53
     **/
    UserConfigDto change2Dto(UserConfig userConfig);
    
    /**
     * DTO to Entity
     *
     * @param userConfigDto
     * @return UserConfig
     * @author chenxiangyue 2026-04-01 23:29:53
     **/
    UserConfig changeDto2(UserConfigDto userConfigDto);
    
    /**
     * DTO to VO
     *
     * @param userConfigDto
     * @return UserConfigVO
     * @author chenxiangyue 2026-04-01 23:29:53
     **/
    UserConfigVo changeDto2Vo(UserConfigDto userConfigDto);
    
    /**
     * vo to dto
     *
     * @param userConfigVo
     * @return UserConfigDTO
     * @author chenxiangyue 2026-04-01 23:29:53
     **/
    UserConfigDto changeVo2Dto(UserConfigVo userConfigVo);
    
    /**
     * Entity to VO
     *
     * @param userConfig
     * @return UserConfigVO
     * @author chenxiangyue 2026-04-01 23:29:53
     **/
    UserConfigVo change2Vo(UserConfig userConfig);
    
}
