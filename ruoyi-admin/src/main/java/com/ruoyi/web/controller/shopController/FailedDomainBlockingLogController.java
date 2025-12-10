package com.ruoyi.web.controller.shopController;


import com.ruoyi.system.service.IFailedDomainBlockingLogService;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;

/**
 * [域名屏蔽未成功记录表控制器]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 13:40:06
 **/
@Api(tags = "域名屏蔽未成功记录表")
@RestController
@RequestMapping("failedDomainBlockingLog")
public class FailedDomainBlockingLogController {

    @Resource(name = "failedDomainBlockingLogService")
    IFailedDomainBlockingLogService failedDomainBlockingLogService;


}
