package com.ruoyi.web.controller.shopController;


import cn.hutool.core.util.StrUtil;
import com.ruoyi.system.domain.dto.ScheduledDomainLockingTimeDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IScheduledDomainLockingTimeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * [域名屏蔽重启节点预约时间控制器]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 14:13:39
 **/
@Api(tags = "域名屏蔽重启节点预约时间")
@RestController
@RequestMapping("scheduledDomainLockingTime")
public class ScheduledDomainLockingTimeController {

    @Resource(name = "scheduledDomainLockingTimeService")
    IScheduledDomainLockingTimeService scheduledDomainLockingTimeService;


    @PostMapping("/add")
    @ApiOperation("添加域名屏蔽重启节点预约时间")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result add(@RequestBody @Valid ScheduledDomainLockingTimeDto scheduledDomainLockingTime, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return scheduledDomainLockingTimeService.add(scheduledDomainLockingTime);
    }


    @DeleteMapping("/deleteById")
    @ApiOperation("删除域名屏蔽重启节点预约时间")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result deleteById(String id) {
        if (StrUtil.isBlank(id)){
            return Result.fail("请选择要删除的预约时间");
        }
        return scheduledDomainLockingTimeService.deleteById(id);
    }

    @GetMapping("/page")
    @ApiOperation("分页查询域名屏蔽重启节点预约时间")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result page(ScheduledDomainLockingTimeDto scheduledDomainLockingTimeDto) {
        if (scheduledDomainLockingTimeDto.getPageNum() == null || scheduledDomainLockingTimeDto.getPageNum() < 1){
            scheduledDomainLockingTimeDto.setPageNum(1L);
        }
        if (scheduledDomainLockingTimeDto.getPageSize() == null || scheduledDomainLockingTimeDto.getPageSize() < 1){
            scheduledDomainLockingTimeDto.setPageSize(10L);
        }
        return scheduledDomainLockingTimeService.page(scheduledDomainLockingTimeDto);
    }

}
