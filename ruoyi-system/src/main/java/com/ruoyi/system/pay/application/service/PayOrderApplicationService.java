package com.ruoyi.system.pay.application.service;

import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.pay.application.dto.request.*;
import com.ruoyi.system.pay.application.vo.CreatePayOrderVo;
import com.ruoyi.system.pay.application.vo.QueryPayOrderVo;
import com.ruoyi.system.pay.application.vo.RefundResultVo;
import com.ruoyi.system.pay.strategy.PayStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * [支付订单应用服务]
 * 所有业务逻辑委托给PayStrategy，由策略模式根据配置选择V1/V2实现
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Slf4j
@Service
public class PayOrderApplicationService {

    @Resource
    private PayStrategy payStrategy;

    public CreatePayOrderVo createOrder(CreatePayOrderRequest request) {
        return payStrategy.createOrder(request);
    }

    public Result<String> pagePay(PagePayRequest request) {
        return payStrategy.pagePay(request);
    }

    public Result<QueryPayOrderVo> queryOrder(QueryPayOrderRequest request) {
        return payStrategy.queryOrder(request);
    }

    public Result<?> refundOrder(RefundPayOrderRequest request) {
        return payStrategy.refundOrder(request);
    }

    public CreatePayOrderVo createPayForOrder(Order order, String payType) {
        return payStrategy.createPayForOrder(order, payType);
    }

    public CreatePayOrderVo createPayForOrderWithVirtualId(Order order, String payType, String virtualOrderId) {
        return payStrategy.createPayForOrderWithVirtualId(order, payType, virtualOrderId);
    }

    public RefundResultVo refundByOrder(OrderRefundRequest request) {
        return payStrategy.refundByOrder(request);
    }

    public boolean verifyNotifySign(Map<String, String> params) {
        return payStrategy.verifyNotifySign(params);
    }
}
