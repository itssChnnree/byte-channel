package com.ruoyi.system.pay.strategy;

import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.pay.application.dto.request.*;
import com.ruoyi.system.pay.application.vo.CreatePayOrderVo;
import com.ruoyi.system.pay.application.vo.QueryPayOrderVo;
import com.ruoyi.system.pay.application.vo.RefundResultVo;

import java.util.Map;

/**
 * [支付策略接口]
 * V1: 小白云OS - MD5签名
 * V2: 佳梦云付 - RSA-SHA256签名
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/4/6
 */
public interface PayStrategy {

    /** 统一下单（API支付） */
    CreatePayOrderVo createOrder(CreatePayOrderRequest request);

    /** 页面跳转支付 */
    Result<String> pagePay(PagePayRequest request);

    /** 查询订单 */
    Result<QueryPayOrderVo> queryOrder(QueryPayOrderRequest request);

    /** 订单退款 */
    Result<?> refundOrder(RefundPayOrderRequest request);

    /** 为订单创建支付 */
    CreatePayOrderVo createPayForOrder(Order order, String payType);

    /** 为订单创建支付（使用虚拟订单号） */
    CreatePayOrderVo createPayForOrderWithVirtualId(Order order, String payType, String virtualOrderId);

    /** 根据订单ID执行退款 */
    RefundResultVo refundByOrder(OrderRefundRequest request);

    /** 验证回调签名 */
    boolean verifyNotifySign(Map<String, String> params);
}
