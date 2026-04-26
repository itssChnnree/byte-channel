package com.ruoyi.web.controller.shopController;


import cn.hutool.core.util.StrUtil;
import com.ruoyi.system.domain.dto.ProcessQuoteDto;
import com.ruoyi.system.domain.dto.TicketMainTextQuoteDto;
import com.ruoyi.system.domain.dto.UpdateQuoteRecordDto;
import com.ruoyi.system.group.InsertGroup;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IOrderQuoteService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;

/**
 * [订单报价表控制器]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-02-27 00:11:14
 **/
@Api(tags = "订单报价表")
@RestController
@RequestMapping("orderQuote")
public class OrderQuoteController {

    @Resource(name = "orderQuoteService")
    IOrderQuoteService orderQuoteService;

    //------------------------------------------------------报价-------------------------------------------------------



    @GetMapping("/cancelQuoteOrder")
    @ApiOperation("报价订单取消")
    public Result cancelQuoteOrder(String orderId,Boolean refoundToBalance){
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        return orderQuoteService.cancelQuoteOrder(orderId,refoundToBalance);
    }


    @ApiOperation("报价订单第三方支付或余额支付")
    @PostMapping("/quoteOrderIsPay")
    public Result quoteOrderIsPay(String orderId,Boolean isBalance) {
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        if (isBalance == null){
            return Result.fail("请选择支付方式");
        }
        return orderQuoteService.quoteOrderIsPay(orderId,isBalance);
    }

    @GetMapping("/getQuoteOrderInfo")
    @ApiOperation("支付页-报价订单查询")
    public Result getQuoteOrderInfo(String orderId){
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        return orderQuoteService.getQuoteOrderInfo(orderId);
    }


    @GetMapping("/getQuoteOrderRecord")
    @ApiOperation("订单信息页-报价处理记录查询")
    public Result getQuoteOrderRecord(String orderId){
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        return orderQuoteService.getQuoteOrderRecord(orderId);
    }


    @PostMapping("/createOrderQuote")
    @ApiOperation("创建报价订单")
    public Result createOrderQuote(@RequestBody @Validated(InsertGroup.class) TicketMainTextQuoteDto ticketMainTextQuoteDto
            , BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return orderQuoteService.createOrderQuote(ticketMainTextQuoteDto);
    }

    @PostMapping("/processQuoteOrder")
    @ApiOperation("处理报价订单")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result processQuoteOrder(@RequestBody ProcessQuoteDto processQuoteDto) {
        if (StrUtil.isBlank(processQuoteDto.getOrderId())) {
            return Result.fail("请选择订单");
        }
        if (StrUtil.isBlank(processQuoteDto.getQuoteProcessingRecord())) {
            return Result.fail("请输入处理记录");
        }
        return orderQuoteService.processQuoteOrder(processQuoteDto);
    }

    @PostMapping("/updateQuoteRecord")
    @ApiOperation("修改报价处理记录")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result updateQuoteRecord(@RequestBody UpdateQuoteRecordDto updateQuoteRecordDto) {
        if (StrUtil.isBlank(updateQuoteRecordDto.getOrderId())) {
            return Result.fail("请选择订单");
        }
        if (StrUtil.isBlank(updateQuoteRecordDto.getQuoteProcessingRecord())) {
            return Result.fail("请输入处理记录");
        }
        return orderQuoteService.updateQuoteRecord(updateQuoteRecordDto);
    }

}
