package com.ruoyi.system.service;


import com.ruoyi.system.domain.dto.RechargeDto;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.http.Result;

import java.math.BigDecimal;

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

    /**
     * [增加余额-从订单]
     * @author 陈湘岳 2026/3/11
     * @param order 订单
     * @return java.lang.Boolean
     **/
    Boolean addBalance(Order order);

    /**
     * [增加余额-通用]
     * @author 陈湘岳 2026/3/11
     * @param amount 金额
     * @param type 类型
     * @return java.lang.Boolean
     **/
    Boolean addBalance(BigDecimal amount, String type, String userId);

    /**
     * [余额充值订单已支付]
     * @author 陈湘岳 2026/3/7
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     **/
    Result rechargeOrderIsPay(String orderId);

    /**
     * [支付页-充值订单查询]
     * @author 陈湘岳 2026/3/7
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     **/
    Result getRechargeOrderInfo(String orderId);

    /**
     * [取消充值订单]
     * @author 陈湘岳 2026/3/7
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     **/
    Result cancelRechargeOrder(String orderId);

    /**
     * [创建充值订单]
     * @author 陈湘岳 2026/3/7
     * @param rechargeDto 床参数
     * @return com.ruoyi.system.http.Result
     **/
    Result createOrderRecharge(RechargeDto rechargeDto);

    /**
     * [查询是否仅退款到余额，及退款费率]
     * @author 陈湘岳 2026/4/1
     * @param
     * @return com.ruoyi.system.http.Result
     **/
    Result findOnlyRefoundBalance();
}
