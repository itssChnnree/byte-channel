package com.ruoyi.web.controller.shopController;


import com.ruoyi.system.service.IScheduledDomainLockingTimeService;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;

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


}
