package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.dto.log.MonitorLogDTO;
import com.ruoyi.system.domain.dto.log.SysUserOperLogDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IUserOperLogService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * [日志保存]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2024/2/20
 */
@RestController
@RequestMapping("/operLog")
public class LogController {

    @Resource
    private IUserOperLogService iUserOperLogService;


    @PostMapping("/getDateHistogram")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    @ApiOperation(value = "查询用户操作日志",httpMethod = "POST")
    public Result getDateHistogram(@RequestBody SysUserOperLogDto sysUserOperLogDTO){
        return iUserOperLogService.getDateHistogram(sysUserOperLogDTO);
    }

}
