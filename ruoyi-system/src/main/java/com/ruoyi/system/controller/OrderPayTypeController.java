package com.ruoyi.system.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.OrderPayTypeDto;
import com.ruoyi.system.service.IOrderPayTypeService;
import com.ruoyi.system.domain.vo.OrderPayTypeVo;
import com.ruoyi.system.domain.entity.OrderPayType;
import com.ruoyi.system.mapstruct.OrderPayTypeMapstruct;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

/**
 * [订单支付方式表控制器]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-23 20:49:25
 **/
@Api(tags = "订单支付方式表")
@RestController
@RequestMapping("orderPayType")
public class OrderPayTypeController {

    @Resource(name = "orderPayTypeService")
    IOrderPayTypeService orderPayTypeService;


}
