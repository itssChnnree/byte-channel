package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.constant.BlockDomainConstant;
import com.ruoyi.system.constant.EntityStatus;
import com.ruoyi.system.domain.dto.BlackDomainArr;
import com.ruoyi.system.domain.dto.ResourceBlockDomainDto;
import com.ruoyi.system.domain.entity.FailedDomainBlockingLog;
import com.ruoyi.system.domain.entity.ScheduledDomainLockingTime;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.FailedDomainBlockingLogMapper;
import com.ruoyi.system.mapper.ResourceBlockDomainMapper;
import com.ruoyi.system.mapper.ScheduledDomainLockingTimeMapper;
import com.ruoyi.system.mapper.ServerResourcesMapper;
import com.ruoyi.system.mapstruct.ResourceBlockDomainMapstruct;
import com.ruoyi.system.service.IResourceBlockDomainService;
import com.ruoyi.system.domain.entity.ResourceBlockDomain;
import com.ruoyi.system.domain.vo.ResourceBlockDomainVo;
import com.ruoyi.system.util.XrayManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资源屏蔽域名表(ResourceBlockDomain)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 13:40:07
 */
@Service("resourceBlockDomainService")
public class ResourceBlockDomainServiceImpl implements IResourceBlockDomainService {


    @Resource
    private ResourceBlockDomainMapper resourceBlockDomainMapper;

    @Resource
    private ResourceBlockDomainMapstruct resourceBlockDomainMapstruct;

    @Resource
    private ServerResourcesMapper serverResourcesMapper;

    @Resource
    private FailedDomainBlockingLogMapper failedDomainBlockingLogMapper;


    @Resource
    private ScheduledDomainLockingTimeMapper scheduledDomainLockingTimeMapper;

    /**
     * [新增]
     *
     * @param resourceBlockDomainDto
     * @return java.lang.String
     * @author 陈湘岳 2025/12/8
     **/
    @Override
    @Transactional
    public Result add(ResourceBlockDomainDto resourceBlockDomainDto) {
        if (!BlockDomainConstant.LIST.contains(resourceBlockDomainDto.getPrefixType())){
            return Result.fail("前缀类型不存在");
        }
        ResourceBlockDomain resourceBlockDomain = resourceBlockDomainMapstruct.changeDto2(resourceBlockDomainDto);
        ResourceBlockDomain byPrefixTypeAndDomain = resourceBlockDomainMapper.findByPrefixTypeAndDomain(resourceBlockDomain.getPrefixType(), resourceBlockDomain.getDomain());
        if (byPrefixTypeAndDomain != null){
            return Result.fail("该配置已存在");
        }
        int insert = resourceBlockDomainMapper.insert(resourceBlockDomain);
        if (insert <= 0){
            throw new RuntimeException("新增屏蔽域名失败");
        }
        //判断是否加入最近更新计划
        if(Boolean.TRUE.equals(resourceBlockDomainDto.getIsAddToRecentUpdatePlan())){
            //查询最近的未处理的预约时间
            ScheduledDomainLockingTime minScheduledTime = scheduledDomainLockingTimeMapper.findMinScheduledTime(EntityStatus.UNHANDLED);
            if (minScheduledTime == null){
                throw new RuntimeException("不存在最近未处理预约时间");
            }
            return Result.success("新增屏蔽域名成功");
        }else {
            //判断是否不加入最近更新计划并且没有预约更新时间，若未预约则立即刷新，已预约则后续刷新
            if (resourceBlockDomainDto.getScheduleTime() == null){
                flush();
            }else {
                ScheduledDomainLockingTime scheduledDomainLockingTime = getScheduledDomainLockingTime(resourceBlockDomainDto);
                int insert1 = scheduledDomainLockingTimeMapper.insert(scheduledDomainLockingTime);
                if (insert1<=0){
                    throw new RuntimeException("预约时间添加失败");
                }
            }
            return Result.success("新增屏蔽域名成功");
        }
    }


    private ScheduledDomainLockingTime getScheduledDomainLockingTime(ResourceBlockDomainDto resourceBlockDomainDto){
        ScheduledDomainLockingTime scheduledDomainLockingTime = new ScheduledDomainLockingTime();
        scheduledDomainLockingTime.setScheduledTime(resourceBlockDomainDto.getScheduleTime());
        scheduledDomainLockingTime.setStatus(EntityStatus.UNHANDLED);
        return scheduledDomainLockingTime;
    }


    private FailedDomainBlockingLog getFailedDomainBlockingLog(String resourcesId,String reason){
        FailedDomainBlockingLog failedDomainBlockingLog = new FailedDomainBlockingLog();
        failedDomainBlockingLog.setServerResourcesId(resourcesId);
        failedDomainBlockingLog.setFailReason(reason);
        failedDomainBlockingLog.setStatus(EntityStatus.UNHANDLED);
        return failedDomainBlockingLog;
    }

    //刷新所有节点资源
    private void flush(){
        List<ResourceBlockDomain> allNormal = resourceBlockDomainMapper.findAllNormal(EntityStatus.NORMAL);
        List<String> collect = allNormal.stream().map(resourceBlockDomain ->
                resourceBlockDomain.getPrefixType() + ":" + resourceBlockDomain.getDomain()).collect(Collectors.toList());
        BlackDomainArr blackDomainArr = new BlackDomainArr();
        blackDomainArr.setDomains(collect);
        //转json
        String domainList = JSON.toJSONString(blackDomainArr);
        List<ServerResources> allServerResourcesList = serverResourcesMapper.findAll();
        allServerResourcesList.parallelStream().forEach(serverResources -> {
            String resourcesIp = serverResources.getResourcesIp();
            //调用节点方法
            String post = XrayManager.updateBlockDomains(resourcesIp, domainList);
            FailedDomainBlockingLog failedDomainBlockingLog = null;
            if (StrUtil.isBlank(post)){
                failedDomainBlockingLog = getFailedDomainBlockingLog(serverResources.getId(), "调用接口失败");
            }
            Result result = JSON.parseObject(post, Result.class);
            if (result.getCode() != 200){
                failedDomainBlockingLog = getFailedDomainBlockingLog(serverResources.getId(), result.getMessage());
            }
            if (ObjectUtil.isNull(failedDomainBlockingLog)){
                failedDomainBlockingLogMapper.insert(failedDomainBlockingLog);
            }
        });

    }
}
