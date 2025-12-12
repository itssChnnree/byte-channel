package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.system.constant.EntityStatus;
import com.ruoyi.system.domain.base.PageUtil;
import com.ruoyi.system.domain.dto.ScheduledDomainLockingTimeDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.ScheduledDomainLockingTimeMapper;
import com.ruoyi.system.mapstruct.ScheduledDomainLockingTimeMapstruct;
import com.ruoyi.system.service.IScheduledDomainLockingTimeService;
import com.ruoyi.system.domain.entity.ScheduledDomainLockingTime;
import com.ruoyi.system.domain.vo.ScheduledDomainLockingTimeVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 域名屏蔽重启节点预约时间(ScheduledDomainLockingTime)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 14:13:39
 */
@Service("scheduledDomainLockingTimeService")
public class ScheduledDomainLockingTimeServiceImpl implements IScheduledDomainLockingTimeService {


    @Resource
    private  ScheduledDomainLockingTimeMapper scheduledDomainLockingTimeMapper;

    @Resource
    private ScheduledDomainLockingTimeMapstruct scheduledDomainLockingTimeMapstruct;

    /**
     * [新增]
     *
     * @param scheduledDomainLockingTimeDto
     * @return java.lang.String
     * @author 陈湘岳 2025/12/8
     **/
    @Override
    public Result add(ScheduledDomainLockingTimeDto scheduledDomainLockingTimeDto) {
        ScheduledDomainLockingTime scheduledDomainLockingTime = scheduledDomainLockingTimeMapstruct.changeDto2(scheduledDomainLockingTimeDto);
        scheduledDomainLockingTime.setStatus(EntityStatus.UNHANDLED);
        int insert = scheduledDomainLockingTimeMapper.insert(scheduledDomainLockingTime);
        return insert > 0 ? Result.success(scheduledDomainLockingTime) : Result.fail("新增失败");
    }

    /**
     * [根据id删除预约时间]
     *
     * @param id 预约时间id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/12/11
     **/
    @Override
    public Result deleteById(String id) {
        int i = scheduledDomainLockingTimeMapper.deleteById(id);
        return i > 0 ? Result.success("删除成功") : Result.fail("删除失败");
    }

    /**
     * [分页查询]
     *
     * @param scheduledDomainLockingTimeDto
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/12/11
     **/
    @Override
    public Result page(ScheduledDomainLockingTimeDto scheduledDomainLockingTimeDto) {
        PageUtils.startPage(scheduledDomainLockingTimeDto);
        List<ScheduledDomainLockingTimeVo> scheduledDomainLockingTimeVos = scheduledDomainLockingTimeMapper.pageList(scheduledDomainLockingTimeDto);
        return null;
    }
}
