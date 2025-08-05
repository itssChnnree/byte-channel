package com.ruoyi.web.controller.shopController;

import com.ruoyi.system.service.IWalletBalanceDetailService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * [˵��/����]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:37
 **/
@Api(tags = "钱包余额明细表")
@RestController
@RequestMapping("walletBalanceDetail")

public class WalletBalanceDetailController{

    @Resource(name = "walletBalanceDetailService")
    IWalletBalanceDetailService walletBalanceDetailService;


}
