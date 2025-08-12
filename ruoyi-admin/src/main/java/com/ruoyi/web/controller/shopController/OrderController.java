package com.ruoyi.web.controller.shopController;

import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.dto.OrderDto;
import com.ruoyi.system.group.InsertGroup;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * [˵��/����]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:02
 **/
@Api(tags = "订单表")
@RestController
@RequestMapping("order")
public class OrderController {

    @Resource
    IOrderService orderService;



    @PostMapping("/createOrderByCommodity")
    @ApiOperation("直接从商品创建订单")
    public Result createOrderByCommodity(@RequestBody @Validated(InsertGroup.class) OrderByCommodityDto orderByCommodityDto
            , BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return orderService.createOrderByCommodity(orderByCommodityDto);
    }

}
