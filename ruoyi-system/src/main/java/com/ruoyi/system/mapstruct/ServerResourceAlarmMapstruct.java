package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import com.ruoyi.system.domain.entity.ServerResourceAlarm;
import com.ruoyi.system.domain.dto.ServerResourceAlarmDto;
import com.ruoyi.system.domain.vo.ServerResourceAlarmVo;

/**
 * 服务器资源故障告警表(ServerResourceAlarm)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-18 23:40:01
 */
@Mapper(componentModel = "spring")
public interface ServerResourceAlarmMapstruct {
    ServerResourceAlarmMapstruct INSTANCE = Mappers.getMapper(ServerResourceAlarmMapstruct.class);

    /**
     * Entity to DTO
     *
     * @param serverResourceAlarm
     * @return ServerResourceAlarmDTO
     * @author chenxiangyue 2026-03-18 23:40:01
     **/
    ServerResourceAlarmDto change2Dto(ServerResourceAlarm serverResourceAlarm);

    /**
     * DTO to Entity
     *
     * @param serverResourceAlarmDto
     * @return ServerResourceAlarm
     * @author chenxiangyue 2026-03-18 23:40:01
     **/
    ServerResourceAlarm changeDto2(ServerResourceAlarmDto serverResourceAlarmDto);

    /**
     * DTO to VO
     *
     * @param serverResourceAlarmDto
     * @return ServerResourceAlarmVO
     * @author chenxiangyue 2026-03-18 23:40:01
     **/
    ServerResourceAlarmVo changeDto2Vo(ServerResourceAlarmDto serverResourceAlarmDto);

    /**
     * vo to dto
     *
     * @param serverResourceAlarmVo
     * @return ServerResourceAlarmDTO
     * @author chenxiangyue 2026-03-18 23:40:01
     **/
    ServerResourceAlarmDto changeVo2Dto(ServerResourceAlarmVo serverResourceAlarmVo);

    /**
     * Entity to VO
     *
     * @param serverResourceAlarm
     * @return ServerResourceAlarmVO
     * @author chenxiangyue 2026-03-18 23:40:01
     **/
    ServerResourceAlarmVo change2Vo(ServerResourceAlarm serverResourceAlarm);

}
