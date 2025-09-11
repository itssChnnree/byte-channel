package com.ruoyi.web.controller.shopController;


import com.ruoyi.system.service.IResourceAllocationTemporaryStorageService;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;

/**
 * [˵��/����]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-11 15:28:59
 **/
@Api(tags = "资源暂存表")
@RestController
@RequestMapping("resourceAllocationTemporaryStorage")
public class ResourceAllocationTemporaryStorageController  {

    @Resource(name = "resourceAllocationTemporaryStorageService")
    IResourceAllocationTemporaryStorageService resourceAllocationTemporaryStorageService;



}
