package com.ruoyi.web.controller.shopController;

import com.ruoyi.system.domain.base.PageBase;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IWalletBalanceDetailService;
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
 * @date 2025-07-20 23:24:37
 **/
@Api(tags = "钱包余额明细表")
@RestController
@RequestMapping("walletBalanceDetail")

public class WalletBalanceDetailController{

    @Resource(name = "walletBalanceDetailService")
    IWalletBalanceDetailService walletBalanceDetailService;

    @GetMapping("/page")
    @ApiOperation("分页查询钱包余额明细")
    public Result page(PageBase pageBase){
        if (pageBase.getPageNum()== null|| pageBase.getPageNum() < 1L){
            pageBase.setPageNum(1L);
        }
        if (pageBase.getPageSize()== null|| pageBase.getPageSize() < 1L){
            pageBase.setPageSize(10L);
        }
        return walletBalanceDetailService.page(pageBase);
    }
}
