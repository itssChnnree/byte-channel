package com.ruoyi.system.service.impl;


import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.WalletBalanceMapper;
import com.ruoyi.system.service.IWalletBalanceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 钱包余额表(WalletBalance)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:32
 */
@Service("walletBalanceService")
public class WalletBalanceServiceImpl implements IWalletBalanceService {


    @Resource
    private WalletBalanceMapper walletBalanceMapper;

    /**
     * [查询余额]
     *
     * @return java.lang.String
     * @author 陈湘岳 2025/8/28
     **/
    @Override
    public Result getWalletBalance() {
        //查询用户余额
        String strUserId = SecurityUtils.getStrUserId();
        BigDecimal byUserId = walletBalanceMapper.findByUserId(strUserId);
        return Result.success(byUserId);
    }
}
