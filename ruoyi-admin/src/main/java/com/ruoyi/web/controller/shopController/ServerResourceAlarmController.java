package com.ruoyi.web.controller.shopController;


import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.ruoyi.system.domain.dto.IdDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.domain.dto.ServerResourceAlarmDto;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.ServerResourceAlarm;
import com.ruoyi.system.mapper.ServerResourceAlarmMapper;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.domain.vo.ServerResourceAlarmVo;
import com.ruoyi.system.service.IServerResourceAlarmService;
import com.ruoyi.system.service.IServerResourcesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * [服务器资源故障告警表控制器]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-18 23:40:02
 **/
@Api(tags = "服务器资源故障告警表")
@RestController
@RequestMapping("serverResourceAlarm")
public class ServerResourceAlarmController {

    @Resource(name = "serverResourceAlarmService")
    IServerResourceAlarmService serverResourceAlarmService;

    @Resource
    ServerResourceAlarmMapper serverResourceAlarmMapper;

    @Resource
    IServerResourcesService iServerResourcesService;

    @PostMapping("/list")
    @ApiOperation("分页查询故障列表")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result list(@RequestBody ServerResourceAlarmDto dto) {
        PageInfo<ServerResourceAlarmVo> pageInfo = serverResourceAlarmService.pageNotEndAlarm(dto);
        return Result.success(pageInfo);
    }

    @PostMapping("/startProcess")
    @ApiOperation("开始处理故障")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result startProcess(@RequestBody IdDto idDto) {
        if (StrUtil.isBlank(idDto.getId())) {
            return Result.fail("请选择故障记录");
        }
        ServerResourceAlarm alarm = serverResourceAlarmMapper.selectById(idDto.getId());
        if (alarm == null) {
            return Result.fail("故障记录不存在");
        }
        if (!"PENDING".equals(alarm.getAlarmStatus())) {
            return Result.fail("只有待处理的故障才能开始处理");
        }
        alarm.setAlarmStatus("PROCESSING");
        serverResourceAlarmMapper.updateById(alarm);
        return Result.success();
    }

    @PostMapping("/resolve")
    @ApiOperation("处理完成故障")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result resolve(@RequestBody IdDto idDto) {
        if (StrUtil.isBlank(idDto.getId())) {
            return Result.fail("请选择故障记录");
        }
        return serverResourceAlarmService.resolve(idDto);
    }

    @GetMapping("/resourceDetail/{alarmId}")
    @ApiOperation("获取故障关联的资源详情")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result resourceDetail(@PathVariable String alarmId) {
        ServerResourceAlarm alarm = serverResourceAlarmMapper.selectById(alarmId);
        if (alarm == null) {
            return Result.fail("故障记录不存在");
        }
        return iServerResourcesService.getById(alarm.getResourceId());
    }

    @PostMapping("/check/{alarmId}")
    @ApiOperation("对故障资源进行检测")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result check(@PathVariable String alarmId) {
        ServerResourceAlarm alarm = serverResourceAlarmMapper.selectById(alarmId);
        if (alarm == null) {
            return Result.fail("故障记录不存在");
        }
        // 调用资源检测逻辑
        return Result.success("检测已触发");
    }

}
