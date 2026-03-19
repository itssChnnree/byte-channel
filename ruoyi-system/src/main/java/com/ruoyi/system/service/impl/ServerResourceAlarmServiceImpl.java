package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.system.constant.FaultHandingStatus;
import com.ruoyi.system.domain.base.PageBase;
import com.ruoyi.system.domain.dto.ServerResourceAlarmDto;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.mapper.ServerResourceAlarmMapper;
import com.ruoyi.system.service.IServerResourceAlarmService;
import com.ruoyi.system.domain.entity.ServerResourceAlarm;
import com.ruoyi.system.domain.vo.ServerResourceAlarmVo;
import com.ruoyi.system.service.IServerResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务器资源故障告警表(ServerResourceAlarm)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-18 23:40:02
 */
@Service("serverResourceAlarmService")
public class ServerResourceAlarmServiceImpl implements IServerResourceAlarmService {


    @Resource
    private ServerResourceAlarmMapper serverResourceAlarmMapper;

    @Resource
    private IServerResourcesService  iServerResourcesService;

    /**
     * [未完成故障告警列表查询-分页查询]
     *
     * @param serverResourceAlarmDto 查询参数
     * @return java.util.List<com.ruoyi.system.domain.vo.ServerResourceAlarmVo>
     * @author 陈湘岳 2026/3/18
     **/
    @Override
    public PageInfo<ServerResourceAlarmVo> pageNotEndAlarm(ServerResourceAlarmDto serverResourceAlarmDto) {
        PageHelper.startPage(serverResourceAlarmDto);
        List<ServerResourceAlarmVo> serverResourceAlarmVos = serverResourceAlarmMapper.selectNotEndAlarmVoList(serverResourceAlarmDto);
        return new PageInfo<>(serverResourceAlarmVos);
    }

    /**
     * [查询未完成故障告警列表]
     *
     * @return java.util.List<com.ruoyi.system.domain.entity.ServerResourceAlarm>
     * @author 陈湘岳 2026/3/19
     **/
    @Override
    public List<ServerResourceAlarm> listNotEndAlarm() {
        return serverResourceAlarmMapper.selectNotEndAlarmList(null);
    }

    /**
     * [资源告警自愈]
     *
     * @param serverResources 资源id
     * @return void
     * @author 陈湘岳 2026/3/19
     **/
    @Override
    public void repairResourceAlarm(ServerResources serverResources) {
        List<ServerResourceAlarm> serverResourceAlarms = serverResourceAlarmMapper.selectNotEndAlarmList(serverResources.getId());
        List<String> alarmIdList = serverResourceAlarms.stream().map(ServerResourceAlarm::getId).collect(Collectors.toList());
        //订单自愈
        if (CollectionUtils.isEmpty(alarmIdList)){
            return;
        }
        serverResourceAlarmMapper.repairResourceAlarm(alarmIdList, FaultHandingStatus.SELF_HEALING);
    }


    /**
     * [故障检测定时任务执行]
     *
     * @return void
     * @author 陈湘岳 2026/3/19
     **/
    @Override
    public void faultTaskCheckJob() {
        //查询所有未完结告警自愈
        List<ServerResourceAlarm> serverResourceAlarms = listNotEndAlarm();

    }
}
