package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import com.ruoyi.system.domain.entity.FailedDomainBlockingLog;
import com.ruoyi.system.domain.dto.FailedDomainBlockingLogDto;
import com.ruoyi.system.domain.vo.FailedDomainBlockingLogVo;

/**
 * 域名屏蔽未成功记录表(FailedDomainBlockingLog)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 21:47:48
 */
@Mapper(componentModel = "spring")
public interface FailedDomainBlockingLogMapstruct {
    FailedDomainBlockingLogMapstruct INSTANCE = Mappers.getMapper(FailedDomainBlockingLogMapstruct.class);

    /**
     * Entity to DTO
     *
     * @param failedDomainBlockingLog
     * @return FailedDomainBlockingLogDTO
     * @author chenxiangyue 2025-12-08 21:47:48
     **/
    FailedDomainBlockingLogDto change2Dto(FailedDomainBlockingLog failedDomainBlockingLog);

    /**
     * DTO to Entity
     *
     * @param failedDomainBlockingLogDto
     * @return FailedDomainBlockingLog
     * @author chenxiangyue 2025-12-08 21:47:48
     **/
    FailedDomainBlockingLog changeDto2(FailedDomainBlockingLogDto failedDomainBlockingLogDto);

    /**
     * DTO to VO
     *
     * @param failedDomainBlockingLogDto
     * @return FailedDomainBlockingLogVO
     * @author chenxiangyue 2025-12-08 21:47:48
     **/
    FailedDomainBlockingLogVo changeDto2Vo(FailedDomainBlockingLogDto failedDomainBlockingLogDto);

    /**
     * vo to dto
     *
     * @param failedDomainBlockingLogVo
     * @return FailedDomainBlockingLogDTO
     * @author chenxiangyue 2025-12-08 21:47:48
     **/
    FailedDomainBlockingLogDto changeVo2Dto(FailedDomainBlockingLogVo failedDomainBlockingLogVo);

    /**
     * Entity to VO
     *
     * @param failedDomainBlockingLog
     * @return FailedDomainBlockingLogVO
     * @author chenxiangyue 2025-12-08 21:47:48
     **/
    FailedDomainBlockingLogVo change2Vo(FailedDomainBlockingLog failedDomainBlockingLog);

}
