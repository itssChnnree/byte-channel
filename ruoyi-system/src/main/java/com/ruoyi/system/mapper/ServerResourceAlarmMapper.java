package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.dto.ServerResourceAlarmDto;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.domain.vo.ServerResourceAlarmVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.ServerResourceAlarm;

import java.util.List;

/**
 * 服务器资源故障告警表(ServerResourceAlarm
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-18 23:40:01
 */
@Mapper
@Repository
public interface ServerResourceAlarmMapper extends BaseMapper<ServerResourceAlarm> {

    /**
     * [查询未结束的告警]
     * @author 陈湘岳 2026/3/19
     * @param serverResourceAlarmDto
     * @return java.util.List<com.ruoyi.system.domain.vo.ServerResourceAlarmVo>
     **/
    List<ServerResourceAlarmVo> selectNotEndAlarmVoList(ServerResourceAlarmDto serverResourceAlarmDto);


    /**
     * [查询未结束的告警]
     * @author 陈湘岳 2026/3/19
     * @return java.util.List<com.ruoyi.system.domain.vo.ServerResourceAlarmVo>
     **/
    List<ServerResourceAlarm> selectNotEndAlarmList(@Param("resourcesId")String resourcesId);

    /**
     * [资源告警变更]
     * @author 陈湘岳 2026/3/19
     * @param alarmStatus 告警状态
     * @return void
     **/
    int repairResourceAlarm(@Param("alarmIds")List<String> alarmIds,
                            @Param("alarmStatus")String alarmStatus);



}
