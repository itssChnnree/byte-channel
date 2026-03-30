package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.system.constant.FaultHandingStatus;
import com.ruoyi.system.constant.ResourcesStatus;
import com.ruoyi.system.domain.dto.IdDto;
import com.ruoyi.system.domain.dto.ServerResourceAlarmDto;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.domain.entity.ServerResourcesXrayValid;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IServerResourceAlarmService;
import com.ruoyi.system.domain.entity.ServerResourceAlarm;
import com.ruoyi.system.domain.vo.ServerResourceAlarmVo;
import com.ruoyi.system.service.IServerResourcesService;
import com.ruoyi.system.util.LogEsUtil;
import com.ruoyi.system.util.XrayManager;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

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

    @Resource
    private ServerResourcesXrayValidMapper serverResourcesXrayValidMapper;

    @Resource
    private ServerResourcesMapper serverResourcesMapper;

    @Resource
    private IServerResourceAlarmService serverResourceAlarmService;


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
     * @param resourcesId 资源id
     * @return void
     * @author 陈湘岳 2026/3/19
     **/
    @Override
    public void repairResourceAlarm(String resourcesId,String alarmStatus) {
        List<ServerResourceAlarm> serverResourceAlarms = serverResourceAlarmMapper.selectNotEndAlarmList(resourcesId);
        List<String> alarmIdList = serverResourceAlarms.stream().map(ServerResourceAlarm::getId).collect(Collectors.toList());
        //订单自愈
        if (CollectionUtils.isEmpty(alarmIdList)){
            return;
        }
        serverResourceAlarmMapper.repairResourceAlarm(alarmIdList, alarmStatus);
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


    /**
     * [修复资源告警]
     *
     * @param idDto
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/3/30
     **/
    @Override
    public Result resolve(IdDto idDto) {
        ServerResourceAlarm serverResourceAlarm = serverResourceAlarmMapper.selectById(idDto.getId());
        if (serverResourceAlarm == null){
            return Result.fail("故障记录不存在");
        }
        if (!serverResourceAlarm.getAlarmStatus().equals(FaultHandingStatus.PROCESSING)){
            return Result.fail("只有处理中的故障才能处理完成");
        }
        String resourcesId = serverResourceAlarm.getResourceId();
        ServerResourcesXrayValid byResourcesId = serverResourcesXrayValidMapper.findByResourcesId(resourcesId);
        if (byResourcesId == null) {
            // 资源校验不存在  新增资源校验
            ServerResources serverResources = serverResourcesMapper.selectById(resourcesId);
            if (ObjectUtils.isEmpty(serverResources)) {
                serverResourceAlarmService.repairResourceAlarm(resourcesId,FaultHandingStatus.RESOLVED);
                return Result.fail("资源不存在");
            }
            byResourcesId = iServerResourcesService.newResourcesValid(serverResources.getUserId(), serverResources);
        }

        String xrayStatus = "";
        try {
            xrayStatus = XrayManager.checkXrayStatus(byResourcesId.getWebIpPort(), byResourcesId.getXrayPort());
        }catch (Exception e){
            LogEsUtil.error("调用go[CheckXrayStatus]接口失败，错误原因为" + e.getMessage(),e);
        }
        Result result = JSON.parseObject(xrayStatus, Result.class);

        if (!ObjectUtils.isEmpty(result)
                &&!ObjectUtils.isEmpty(result.getData())
                &&"200".equals(result.getData().toString().trim())) {
            serverResourcesMapper.updateResourcesStatus(resourcesId, ResourcesStatus.NORMAL);
            serverResourceAlarmService.repairResourceAlarm(resourcesId,FaultHandingStatus.RESOLVED);
            return Result.success("故障处理完毕",true);
        } else {
            return Result.fail("节点未修复，无法处理完成");
        }
    }


}
