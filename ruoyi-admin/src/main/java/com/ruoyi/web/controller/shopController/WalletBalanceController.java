package com.ruoyi.web.controller.shopController;

import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IWalletBalanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

}
