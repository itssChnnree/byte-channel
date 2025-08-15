package com.ruoyi.web.controller.shopController;

import com.ruoyi.system.service.IOrderCommodityResourcesService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * [所属订单的商品资源]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:07
 **/
@Api(tags = "订单商品资源")
@RestController
@RequestMapping("orderCommodityResources")
public class OrderCommodityResourcesController{

    @Resource(name = "orderCommodityResourcesService")
    IOrderCommodityResourcesService orderCommodityResourcesService;


}
