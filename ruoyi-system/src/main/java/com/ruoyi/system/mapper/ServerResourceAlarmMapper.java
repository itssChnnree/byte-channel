package com.ruoyi.system.mapper;


import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.ServerResourceAlarm;

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

}
