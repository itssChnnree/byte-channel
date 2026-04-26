package com.ruoyi.system.service;


import com.ruoyi.system.domain.dto.RechargeDto;
import com.ruoyi.system.domain.dto.RefundFeeConfigDto;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.http.Result;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    /**
     * [管理页查询退款配置（费率及开关，不做白名单校验）]
     * @author 陈湘岳 2026/4/6
     * @return com.ruoyi.system.http.Result
     **/
    Result findRefundConfig();

    /**
     * [管理员添加退款白名单用户]
     * @param username 用户名
     * @author 陈湘岳 2026/4/6
     **/
    Result<String> addRefundWhitelist(String username);

    /**
     * [查询退款白名单用户，支持用户名模糊查询]
     * @param username 用户名（可选），为空则返回全部
     * @return List<String> 用户名列表
     * @author 陈湘岳 2026/4/6
     **/
    Result<List<String>> getRefundWhitelist(String username);

    /**
     * [管理员删除退款白名单用户]
     * @param username 用户名
     * @author 陈湘岳 2026/4/6
     **/
    void deleteRefundWhitelist(String username);

    /**
     * [管理员更新退款费率及开关配置]
     * 费率：同时更新活跃的Properties和Redis
     * 开关：仅更新Redis
     * @param dto 配置DTO
     * @author 陈湘岳 2026/4/6
     **/
    Result<String> updateRefundFeeConfig(RefundFeeConfigDto dto);
}
