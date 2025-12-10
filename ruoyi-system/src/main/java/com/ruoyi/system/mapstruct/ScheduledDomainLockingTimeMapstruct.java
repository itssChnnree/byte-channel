package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import com.ruoyi.system.domain.entity.ScheduledDomainLockingTime;
import com.ruoyi.system.domain.dto.ScheduledDomainLockingTimeDto;
import com.ruoyi.system.domain.vo.ScheduledDomainLockingTimeVo;

/**
 * 域名屏蔽重启节点预约时间(ScheduledDomainLockingTime)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 14:13:39
 */
@Mapper(componentModel = "spring")
public interface ScheduledDomainLockingTimeMapstruct {
    ScheduledDomainLockingTimeMapstruct INSTANCE = Mappers.getMapper(ScheduledDomainLockingTimeMapstruct.class);

    /**
     * Entity to DTO
     *
     * @param scheduledDomainLockingTime
     * @return ScheduledDomainLockingTimeDTO
     * @author chenxiangyue 2025-12-08 14:13:39
     **/
    ScheduledDomainLockingTimeDto change2Dto(ScheduledDomainLockingTime scheduledDomainLockingTime);

    /**
     * DTO to Entity
     *
     * @param scheduledDomainLockingTimeDto
     * @return ScheduledDomainLockingTime
     * @author chenxiangyue 2025-12-08 14:13:39
     **/
    ScheduledDomainLockingTime changeDto2(ScheduledDomainLockingTimeDto scheduledDomainLockingTimeDto);

    /**
     * DTO to VO
     *
     * @param scheduledDomainLockingTimeDto
     * @return ScheduledDomainLockingTimeVO
     * @author chenxiangyue 2025-12-08 14:13:39
     **/
    ScheduledDomainLockingTimeVo changeDto2Vo(ScheduledDomainLockingTimeDto scheduledDomainLockingTimeDto);

    /**
     * vo to dto
     *
     * @param scheduledDomainLockingTimeVo
     * @return ScheduledDomainLockingTimeDTO
     * @author chenxiangyue 2025-12-08 14:13:39
     **/
    ScheduledDomainLockingTimeDto changeVo2Dto(ScheduledDomainLockingTimeVo scheduledDomainLockingTimeVo);

    /**
     * Entity to VO
     *
     * @param scheduledDomainLockingTime
     * @return ScheduledDomainLockingTimeVO
     * @author chenxiangyue 2025-12-08 14:13:39
     **/
    ScheduledDomainLockingTimeVo change2Vo(ScheduledDomainLockingTime scheduledDomainLockingTime);

}
