package com.ruoyi.web.controller.shopController;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.dto.OrderByRenewalDto;
import com.ruoyi.system.domain.dto.OrderDto;
import com.ruoyi.system.group.InsertGroup;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.*;

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

//------------------------------------------------------新购-------------------------------------------------------

    @PostMapping("/createOrderByCommodity")
    @ApiOperation("从商品创建订单")
    public Result createOrderByCommodity(@RequestBody @Validated(InsertGroup.class) OrderByCommodityDto orderByCommodityDto
            , BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return orderService.createOrderByCommodity(orderByCommodityDto);
    }


    @ApiOperation("第三方支付或余额支付")
    @PostMapping("/orderIsPay")
    public Result orderIsPay(String orderId,Boolean isBalance) {
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        if (isBalance == null){
            return Result.fail("请选择支付方式");
        }
        return orderService.orderIsPay(orderId,isBalance);
    }

    @GetMapping("/cancelOrder")
    @ApiOperation("新购订单取消")
    public Result cancelOrder(String orderId){
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        return orderService.cancelOrderNew(orderId);
    }


    @GetMapping("/getOrderInfo")
    @ApiOperation("新购-支付页详情查询")
    public Result getOrderInfo(String orderId){
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        return orderService.getOrderInfo(orderId);
    }


//------------------------------------------------------续费-------------------------------------------------------


    @PostMapping("/createOrderByRenewal")
    @ApiOperation("资源续费订单创建")
    public Result createOrderByRenewal(@RequestBody @Validated(InsertGroup.class) OrderByRenewalDto orderByRenewalDto
            , BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return orderService.createOrderByRenewal(orderByRenewalDto);
    }


    @GetMapping("/cancelRenewalOrder")
    @ApiOperation("续费订单取消")
    public Result cancelOrderRenewal(String orderId){
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        return orderService.cancelOrderRenewal(orderId);
    }



    @ApiOperation("续费订单第三方支付或余额支付")
    @PostMapping("/renewalOrderIsPay")
    public Result renewalOrderIsPay(String orderId,Boolean isBalance) {
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        if (isBalance == null){
            return Result.fail("请选择支付方式");
        }
        return orderService.renewalOrderIsPay(orderId,isBalance);
    }

    @GetMapping("/getRenewalOrderInfo")
    @ApiOperation("支付页-续费订单查询")
    public Result getRenewalOrderInfo(String orderId){
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        return orderService.getRenewalOrderInfo(orderId);
    }




//-------------------------------------------------------通用-------------------------------------------------------

    @GetMapping("/getOrderStatus")
    @ApiOperation("获取订单状态")
    public Result getOrderStatus(String orderId){
        if (StrUtil.isBlank(orderId)){
            return Result.fail("请选择订单");
        }
        return orderService.getOrderStatus(orderId);
    }


    @ApiOperation("获取支付二维码")
    @GetMapping("/getQrCode")
    public Result getQrCode(String orderId,String payType) {
        if (StrUtil.isBlank(orderId)){
            return Result.fail("请选择订单");
        }
        if(StrUtil.isBlank(payType)){
            return Result.fail("请选择支付方式");
        }
        return orderService.getQrCode(orderId,payType);
    }




    @PostMapping("/pageQuery")
    @ApiOperation("分页查询订单")
    public Result pageQuery(@RequestBody OrderDto orderDto){
        return orderService.pageQuery(orderDto);
    }


    @PostMapping("/pageQueryService")
    @ApiOperation("分页查询订单-客服")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result pageQueryService(@RequestBody OrderDto orderDto){
        return orderService.pageQueryService(orderDto);
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


    @GetMapping("/getOrderDetailById")
    @ApiOperation("获取订单详情-订单信息详情和订单时间线部分")
    public Result getOrderDetailById(String orderId){
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        return orderService.getOrderDetailById(orderId);
    }

}
