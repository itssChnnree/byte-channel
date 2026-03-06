package com.ruoyi.system.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.BalanceDetailStatus;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.entity.WalletBalance;
import com.ruoyi.system.domain.entity.WalletBalanceDetail;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.WalletBalanceDetailMapper;
import com.ruoyi.system.mapper.WalletBalanceMapper;
import com.ruoyi.system.service.IWalletBalanceService;
import com.ruoyi.system.util.LogEsUtil;
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

    @Resource
    private WalletBalanceDetailMapper walletBalanceDetailMapper;

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

    //余额扣减
    public Boolean reduceBalance(Order order){
        String userId = SecurityUtils.getStrUserId();
        //获取用户余额
        //一锁 余额
        WalletBalance walletBalanceByUserId = walletBalanceMapper.findWalletBalanceByUserId(userId);
        //二判 余额是否充足
        if (ObjectUtil.isNull(walletBalanceByUserId)) {
            LogEsUtil.warn("查询用户余额错误,用户id："+userId);
            throw new RuntimeException("查询用户余额错误，请联系管理员");
        }
        if (walletBalanceByUserId.getBalance().compareTo(order.getAmount()) < 0){
            LogEsUtil.info("用户余额不足,用户余额："+walletBalanceByUserId.getBalance()+",用户id："+userId);
            return false;
        }
        //余额充足，则扣减余额
        BigDecimal balance = walletBalanceByUserId.getBalance();
        walletBalanceByUserId.setBalance(balance.subtract(order.getAmount()));
        int i = walletBalanceMapper.updateById(walletBalanceByUserId);
        if (i >= 1){
            //添加余额变更记录
            walletBalanceDetailMapper.insert(buildWalletBalanceDetail(walletBalanceByUserId, order));
            LogEsUtil.info("用户余额变更成功,用户id："+userId+",扣减余额："+order.getAmount()+",扣减后余额："+walletBalanceByUserId.getBalance());
            return true;
        }else {
            LogEsUtil.warn("扣减余额失败，用户id："+userId);
            throw new RuntimeException("余额变更失败，请联系管理员");
        }
    }


    private WalletBalanceDetail buildWalletBalanceDetail(WalletBalance walletBalance, Order order) {
        WalletBalanceDetail walletBalanceDetail = new WalletBalanceDetail();
        walletBalanceDetail.setUserId(walletBalance.getUserId());
        walletBalanceDetail.setChangeAmount(order.getAmount().doubleValue());
        walletBalanceDetail.setType(BalanceDetailStatus.BUY);
        walletBalanceDetail.setNowAmount(walletBalance.getBalance().doubleValue());
        return walletBalanceDetail;
    }
}
