package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.ScheduledDomainLockingTimeDto;
import com.ruoyi.system.domain.entity.ScheduledDomainLockingTime;
import com.ruoyi.system.domain.vo.ScheduledDomainLockingTimeVo;
import com.ruoyi.system.http.Result;

import javax.validation.Valid;


/**
 * 域名屏蔽重启节点预约时间(ScheduledDomainLockingTime)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 14:13:39
 */
public interface IScheduledDomainLockingTimeService {


    /**
     * [新增]
     * @author 陈湘岳 2025/12/8
     * @param scheduledDomainLockingTime
     * @return java.lang.String
     **/
    Result add(ScheduledDomainLockingTimeDto scheduledDomainLockingTime);

    /**
     * [根据id删除预约时间]
     * @author 陈湘岳 2025/12/11
     * @param id 预约时间id
     * @return com.ruoyi.system.http.Result
     **/
    Result deleteById(String id);

    /**
     * [分页查询]
     * @author 陈湘岳 2025/12/11
     * @param scheduledDomainLockingTimeDto
     * @return com.ruoyi.system.http.Result
     **/
    Result page(ScheduledDomainLockingTimeDto scheduledDomainLockingTimeDto);

    /**
     * [根据id修改预约重启时间]
     * @author 陈湘岳 2025/12/12
     * @param scheduledDomainLockingTime 修改参数
     * @return com.ruoyi.system.http.Result
     **/
    Result updateById(ScheduledDomainLockingTimeDto scheduledDomainLockingTime);

    /**
     * [获取最近预约时间]
     * @author 陈湘岳 2025/12/14
     * @param
     * @return com.ruoyi.system.http.Result
     **/
    Result getMinTime();
}
