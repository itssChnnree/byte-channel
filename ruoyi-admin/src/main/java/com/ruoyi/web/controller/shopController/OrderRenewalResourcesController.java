package com.ruoyi.web.controller.shopController;


import com.ruoyi.system.service.IOrderRenewalResourcesService;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;

/**
 * [订单-续费资源表]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-15 23:56:29
 **/
@Api(tags = "订单-续费资源表")
@RestController
@RequestMapping("orderRenewalResources")
public class OrderRenewalResourcesController{

    @Resource(name = "orderRenewalResourcesService")
    IOrderRenewalResourcesService orderRenewalResourcesService;

    

}
