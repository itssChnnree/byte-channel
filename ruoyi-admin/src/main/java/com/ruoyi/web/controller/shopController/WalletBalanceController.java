package com.ruoyi.web.controller.shopController;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.system.domain.dto.RechargeDto;
import com.ruoyi.system.group.InsertGroup;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IWalletBalanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * [˵��/����]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:32
 **/
@Api(tags = "钱包余额表")
@RestController
@RequestMapping("walletBalance")

public class WalletBalanceController{

    @Resource(name = "walletBalanceService")
    IWalletBalanceService walletBalanceService;




    @ApiOperation("查询余额")
    @GetMapping("/getWalletBalance")
    public Result getWalletBalance() {
        return walletBalanceService.getWalletBalance();
    }
    


    @GetMapping("/cancelRechargeOrder")
    @ApiOperation("充值订单取消")
    public Result cancelRechargeOrder(String orderId){
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        return walletBalanceService.cancelRechargeOrder(orderId);
    }


    @ApiOperation("充值订单第三方支付或余额支付")
    @PostMapping("/rechargeOrderIsPay")
    public Result rechargeOrderIsPay(String orderId) {
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        return walletBalanceService.rechargeOrderIsPay(orderId);
    }

    @GetMapping("/getRechargeOrderInfo")
    @ApiOperation("支付页-充值订单查询")
    public Result getRechargeOrderInfo(String orderId){
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        return walletBalanceService.getRechargeOrderInfo(orderId);
    }

    @PostMapping("/createOrderRecharge")
    @ApiOperation("创建充值订单")
    public Result createOrderRecharge(@RequestBody @Validated(InsertGroup.class) RechargeDto rechargeDto
            , BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return walletBalanceService.createOrderRecharge(rechargeDto);
    }

    @GetMapping("/findOnlyRefoundBalance")
    @ApiOperation("查询退款配置")
    public Result findOnlyRefoundBalance() {

        return walletBalanceService.findOnlyRefoundBalance();

    }

}
