package com.ruoyi.web.controller.shopController;


import com.ruoyi.system.service.IOrderCommodityService;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;

/**
 * [˵��/����]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-15 10:01:36
 **/
@Api(tags = "订单商品")
@RestController
@RequestMapping("orderCommodity")
public class OrderCommodityController {

    @Resource(name = "orderCommodityService")
    IOrderCommodityService orderCommodityService;



}
