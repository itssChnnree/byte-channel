package com.ruoyi.web.controller.shopController;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.system.Util.DefaultValueUtil;
import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.dto.OrderByShoppingCartDto;
import com.ruoyi.system.domain.dto.OrderDto;
import com.ruoyi.system.group.InsertGroup;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.parameters.P;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @ApiOperation("从商品创建订单")
    public Result createOrderByCommodity(@RequestBody @Validated(InsertGroup.class) OrderByCommodityDto orderByCommodityDto
            , BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return orderService.createOrderByCommodity(orderByCommodityDto);
    }


    @GetMapping("/cancelOrder")
    @ApiOperation("取消订单")
    public Result cancelOrder(String orderId){
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        return orderService.cancelOrder(orderId);
    }


    @GetMapping("/pageQuery")
    @ApiOperation("分页查询订单")
    public Result pageQuery(@RequestBody OrderDto orderDto){
        return orderService.pageQuery(orderDto);
    }


    @GetMapping("/getOrderInfo")
    @ApiOperation("获取订单信息")
    public Result getOrderInfo(String orderId){
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        return orderService.getOrderInfo(orderId);
    }


    @PostMapping("/calculatePrice")
    @ApiOperation("计算价格")
    public Result calculatePrice(@RequestBody @Validated(InsertGroup.class) OrderByCommodityDto orderByCommodityDto
            , BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return orderService.calculatePrice(orderByCommodityDto);
    }



    @GetMapping("/test")
    public String test(){
        return orderService.test();
    }


}
