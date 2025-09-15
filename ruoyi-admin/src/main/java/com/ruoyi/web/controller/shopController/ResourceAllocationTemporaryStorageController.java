package com.ruoyi.web.controller.shopController;


import com.ruoyi.system.domain.dto.ResourceAllocationTemporaryStorageDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IResourceAllocationTemporaryStorageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;

/**
 * [资源暂存]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-11 15:28:59
 **/
@Api(tags = "资源暂存表")
@RestController
@RequestMapping("/resourceAllocationTemporaryStorage")
public class ResourceAllocationTemporaryStorageController  {

    @Resource(name = "resourceAllocationTemporaryStorageService")
    IResourceAllocationTemporaryStorageService resourceAllocationTemporaryStorageService;



    @PostMapping("/add")
    @ApiOperation("添加资源暂存")
    public Result add(@RequestBody ResourceAllocationTemporaryStorageDto resourceAllocationTemporaryStorageDto) {
        if (!resourceAllocationTemporaryStorageDto.validAuthId()){
            return Result.fail("authId错误");
        }
        return resourceAllocationTemporaryStorageService.add(resourceAllocationTemporaryStorageDto);
    }


    @GetMapping("/page")
    @ApiOperation("分页查询资源暂存")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result page(String ip) {
        return resourceAllocationTemporaryStorageService.page(ip);
    }

    @DeleteMapping("/deleteById")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    @ApiOperation("根据id删除")
    public Result deleteById(String id) {
        return resourceAllocationTemporaryStorageService.deleteById(id);
    }

}
