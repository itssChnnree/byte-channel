package com.ruoyi.system.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.ServerResourceAlarmDto;
import com.ruoyi.system.service.IServerResourceAlarmService;
import com.ruoyi.system.domain.vo.ServerResourceAlarmVo;
import com.ruoyi.system.domain.entity.ServerResourceAlarm;
import com.ruoyi.system.mapstruct.ServerResourceAlarmMapstruct;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

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


}
