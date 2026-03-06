package com.ruoyi.system.service;


import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.http.Result;

/**
 * 钱包余额表(WalletBalance)�����ӿ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:32
 */
public interface IWalletBalanceService{


    /**
     * [查询余额]
     * @author 陈湘岳 2025/8/28
     * @param
     * @return java.lang.String
     **/
    Result getWalletBalance();

    /**
     * [扣减余额]
     * @author 陈湘岳 2026/3/1
     * @param order
     * @return java.lang.Boolean
     **/
    Boolean reduceBalance(Order order);
}
