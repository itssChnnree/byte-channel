package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.ruoyi.system.domain.dto.ServerResourceAlarmDto;
import com.ruoyi.system.domain.entity.ServerResourceAlarm;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.domain.vo.ServerResourceAlarmVo;

import java.util.List;


/**
 * 服务器资源故障告警表(ServerResourceAlarm)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-18 23:40:02
 */
public interface IServerResourceAlarmService {


    /**
     * [未完成故障告警列表查询-controller查询版]
     * @author 陈湘岳 2026/3/18
     * @param serverResourceAlarmDto 查询参数
     * @return java.util.List<com.ruoyi.system.domain.vo.ServerResourceAlarmVo>
     **/
    PageInfo<ServerResourceAlarmVo> pageNotEndAlarm(ServerResourceAlarmDto serverResourceAlarmDto);

    /**
     * [查询未完成故障告警列表]
     * @author 陈湘岳 2026/3/19
     * @param
     * @return java.util.List<com.ruoyi.system.domain.entity.ServerResourceAlarm>
     **/
    List<ServerResourceAlarm> listNotEndAlarm();

    /**
     * [修复资源告警]
     * @author 陈湘岳 2026/3/19
     * @param serverResources 资源id
     * @return void
     **/
    void repairResourceAlarm(ServerResources serverResources);

    /**
     * [故障检测定时任务执行]
     * @author 陈湘岳 2026/3/19
     * @param
     * @return void
     **/
    void faultTaskCheckJob();
}
