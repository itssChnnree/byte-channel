package com.ruoyi.web.controller.shopController;


import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IFailedDomainBlockingLogService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
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


    @GetMapping("/page")
    @ApiOperation("分页查询域名屏蔽未成功记录")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result page(Integer pageNum, Integer pageSize ,String ip , String commodityId, String categoryId) {
        if (pageNum == null || pageNum< 1){
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1){
            pageSize = 10;
        }
        return failedDomainBlockingLogService.page(pageNum,pageSize,ip,commodityId,categoryId);
    }


    @PutMapping("/handle")
    @ApiOperation("处理域名屏蔽未成功记录")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result handle(String id) {
        if (id == null){
            return Result.fail("请选择要处理的记录");
        }
        return failedDomainBlockingLogService.handle(id);
    }
    
}
