package com.ruoyi.web.controller.shopController;


import com.ruoyi.system.service.IServerResourcesXrayValidService;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;

/**
 * [˵��/����]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-05 17:32:31
 **/
@Api(tags = "资源状态检查节点分布")
@RestController
@RequestMapping("serverResourcesXrayValid")
public class ServerResourcesXrayValidController {

    @Resource(name = "serverResourcesXrayValidService")
    IServerResourcesXrayValidService serverResourcesXrayValidService;



}
