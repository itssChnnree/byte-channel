package com.ruoyi.web.controller.shopController;


import com.ruoyi.system.service.IServerResourcesRenewalService;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;

/**
 * [˵��/����]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-10-28 10:48:27
 **/
@Api(tags = "服务器资源自动续费表")
@RestController
@RequestMapping("serverResourcesRenewal")
public class ServerResourcesRenewalController{

    @Resource(name = "serverResourcesRenewalService")
    IServerResourcesRenewalService serverResourcesRenewalService;


}
