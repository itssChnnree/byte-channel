package com.ruoyi.system.pay.strategy;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.system.constant.BalanceDetailStatus;
import com.ruoyi.system.constant.OrderStatus;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.entity.OrderPayType;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.OrderMapper;
import com.ruoyi.system.pay.application.dto.request.*;
import com.ruoyi.system.pay.application.vo.CreatePayOrderVo;
import com.ruoyi.system.pay.application.vo.QueryPayOrderVo;
import com.ruoyi.system.pay.application.vo.RefundResultVo;
import com.ruoyi.system.pay.domain.valueobject.RefundMode;
import com.ruoyi.system.service.IOrderPayTypeService;
import com.ruoyi.system.service.IWalletBalanceService;
import com.ruoyi.system.util.LogEsUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * [支付策略抽象基类]
 * 包含共享的退款业务逻辑，子类只需实现平台差异部分。
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/4/6
 */
@Slf4j
public abstract class AbstractPayStrategy implements PayStrategy {

    @Resource
    protected OrderMapper orderMapper;
    @Resource
    protected IOrderPayTypeService orderPayTypeService;
    @Resource
    protected IWalletBalanceService walletBalanceService;
    @Resource
    protected HttpServletRequest httpServletRequest;

    /** 子类提供微信手续费率 */
    protected abstract BigDecimal getWechatFeeRate();

    /** 子类提供支付宝手续费率 */
    protected abstract BigDecimal getAlipayFeeRate();

    @Override
    public CreatePayOrderVo createPayForOrder(Order order, String payType) {
        return createPayForOrderWithVirtualId(order, payType, order.getId());
    }

    @Override
    public RefundResultVo refundByOrder(OrderRefundRequest request) {
        String orderId = request.getOrderId();
        RefundMode refundMode = RefundMode.fromCode(request.getRefundMode());
        Boolean refundToBalance = request.getRefundToBalance();

        Order order = orderMapper.queryByIdForUpdate(orderId);
        if (order == null) {
            return RefundResultVo.builder().success(false).message("订单不存在").build();
        }

        OrderPayType orderPayType = orderPayTypeService.getByOrderId(orderId);
        String paymentType = order.getPaymentType();
        BigDecimal orderAmount = order.getAmount();
        BigDecimal refundAmount = request.getRefundAmount();
        if (refundAmount == null) {
            refundAmount = orderAmount;
        }
        if (refundAmount.compareTo(orderAmount) > 0) {
            return RefundResultVo.builder().success(false).message("退款金额大于支付金额").build();
        }

        List<RefundResultVo.ChannelRefundResult> results = new ArrayList<>();
        switch (refundMode) {
            case USER_REFUND:
            case SYSTEM_TIMEOUT:
                results = refundAllChannels(order, orderPayType, paymentType, refundAmount, refundToBalance);
                break;
            case ERROR_REFUND:
                results = refundErrorChannels(order, orderPayType, paymentType, refundAmount, refundToBalance);
                break;
        }

        BigDecimal totalRefund = BigDecimal.ZERO;
        for (RefundResultVo.ChannelRefundResult result : results) {
            if (result.getSuccess() && result.getRefundAmount() != null) {
                totalRefund = totalRefund.add(result.getRefundAmount());
            }
        }

        boolean allSuccess = results.stream().allMatch(RefundResultVo.ChannelRefundResult::getSuccess);
        return RefundResultVo.builder()
                .success(allSuccess)
                .totalRefundAmount(totalRefund)
                .channelResults(results)
                .message(allSuccess ? "退款成功" : "部分渠道退款失败")
                .build();
    }

    // ========== 退款辅助方法 ==========

    protected String getClientIp() {
        String ip = httpServletRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = httpServletRequest.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = httpServletRequest.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = httpServletRequest.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private List<RefundResultVo.ChannelRefundResult> refundAllChannels(
            Order order, OrderPayType orderPayType, String paymentType,
            BigDecimal refundAmount, Boolean refundToBalance) {
        List<RefundResultVo.ChannelRefundResult> results = new ArrayList<>();
        if (orderPayType != null && StrUtil.isNotBlank(orderPayType.getWxOrderId())) {
            results.add(refundWechat(order, orderPayType.getWxOrderId(), paymentType, refundAmount, refundToBalance));
        }
        if (orderPayType != null && StrUtil.isNotBlank(orderPayType.getAlipayOrderId())) {
            results.add(refundAlipay(order, orderPayType.getAlipayOrderId(), paymentType, refundAmount, refundToBalance));
        }
        if (OrderStatus.BALANCE_PAY.equals(paymentType)) {
            results.add(refundBalance(order, refundAmount));
        }
        return results;
    }

    private List<RefundResultVo.ChannelRefundResult> refundErrorChannels(
            Order order, OrderPayType orderPayType, String paymentType,
            BigDecimal refundAmount, Boolean refundToBalance) {
        List<RefundResultVo.ChannelRefundResult> results = new ArrayList<>();
        if (orderPayType != null && StrUtil.isNotBlank(orderPayType.getWxOrderId())
                && !OrderStatus.WECHAT_PAY.equals(paymentType)) {
            results.add(refundWechat(order, orderPayType.getWxOrderId(), paymentType, refundAmount, false));
        }
        if (orderPayType != null && StrUtil.isNotBlank(orderPayType.getAlipayOrderId())
                && !OrderStatus.ALIPAY_PAY.equals(paymentType)) {
            results.add(refundAlipay(order, orderPayType.getAlipayOrderId(), paymentType, refundAmount, false));
        }
        return results;
    }

    private RefundResultVo.ChannelRefundResult refundWechat(
            Order order, String virtualOrderId, String paymentType,
            BigDecimal refundAmount, Boolean refundToBalance) {
        QueryPayOrderRequest queryRequest = new QueryPayOrderRequest();
        queryRequest.setOutTradeNo(virtualOrderId);
        Result<QueryPayOrderVo> queryResult = queryOrder(queryRequest);
        if (queryResult.getCode() != 200 || queryResult.getData() == null) {
            return RefundResultVo.ChannelRefundResult.builder()
                    .channel(OrderStatus.WECHAT_PAY).virtualOrderId(virtualOrderId)
                    .refundAmount(BigDecimal.ZERO).success(false).message("查询订单状态失败").build();
        }
        QueryPayOrderVo orderVo = queryResult.getData();
        if (orderVo.getStatus() == null || orderVo.getStatus() != 1) {
            return closeUnpaidOrder(virtualOrderId, OrderStatus.WECHAT_PAY);
        }
        BigDecimal actualRefundAmount;
        if (OrderStatus.WECHAT_PAY.equals(paymentType)) {
            actualRefundAmount = refundToBalance ? refundAmount
                    : refundAmount.multiply(BigDecimal.ONE.subtract(getWechatFeeRate()));
        } else {
            actualRefundAmount = refundAmount;
        }
        return refundToBalance && OrderStatus.WECHAT_PAY.equals(paymentType)
                ? refundToBalance(order, actualRefundAmount, OrderStatus.WECHAT_PAY)
                : refundToOriginalChannel(order, virtualOrderId, actualRefundAmount, OrderStatus.WECHAT_PAY);
    }

    private RefundResultVo.ChannelRefundResult refundAlipay(
            Order order, String virtualOrderId, String paymentType,
            BigDecimal refundAmount, Boolean refundToBalance) {
        QueryPayOrderRequest queryRequest = new QueryPayOrderRequest();
        queryRequest.setOutTradeNo(virtualOrderId);
        Result<QueryPayOrderVo> queryResult = queryOrder(queryRequest);
        if (queryResult.getCode() != 200 || queryResult.getData() == null) {
            return RefundResultVo.ChannelRefundResult.builder()
                    .channel(OrderStatus.ALIPAY_PAY).virtualOrderId(virtualOrderId)
                    .refundAmount(BigDecimal.ZERO).success(false).message("查询订单状态失败").build();
        }
        QueryPayOrderVo orderVo = queryResult.getData();
        if (orderVo.getStatus() == null || orderVo.getStatus() != 1) {
            return closeUnpaidOrder(virtualOrderId, OrderStatus.ALIPAY_PAY);
        }
        BigDecimal actualRefundAmount;
        if (OrderStatus.ALIPAY_PAY.equals(paymentType)) {
            actualRefundAmount = refundToBalance ? refundAmount
                    : refundAmount.multiply(BigDecimal.ONE.subtract(getAlipayFeeRate()));
        } else {
            actualRefundAmount = refundAmount;
        }
        return refundToBalance && OrderStatus.ALIPAY_PAY.equals(paymentType)
                ? refundToBalance(order, actualRefundAmount, OrderStatus.ALIPAY_PAY)
                : refundToOriginalChannel(order, virtualOrderId, actualRefundAmount, OrderStatus.ALIPAY_PAY);
    }

    private RefundResultVo.ChannelRefundResult refundBalance(Order order, BigDecimal refundAmount) {
        try {
            walletBalanceService.addBalance(refundAmount, BalanceDetailStatus.REFUND, order.getUserId());
            return RefundResultVo.ChannelRefundResult.builder()
                    .channel(OrderStatus.BALANCE_PAY).refundAmount(refundAmount)
                    .success(true).message("余额退款成功").build();
        } catch (Exception e) {
            LogEsUtil.error("余额退款失败，订单id：" + order.getId(), e);
            return RefundResultVo.ChannelRefundResult.builder()
                    .channel(OrderStatus.BALANCE_PAY).refundAmount(BigDecimal.ZERO)
                    .success(false).message("余额退款失败：" + e.getMessage()).build();
        }
    }

    private RefundResultVo.ChannelRefundResult refundToBalance(
            Order order, BigDecimal refundAmount, String channel) {
        try {
            walletBalanceService.addBalance(refundAmount, BalanceDetailStatus.REFUND, order.getUserId());
            return RefundResultVo.ChannelRefundResult.builder()
                    .channel(channel).refundAmount(refundAmount)
                    .success(true).message("退款到余额成功").build();
        } catch (Exception e) {
            LogEsUtil.error("退款到余额失败，订单id：" + order.getId(), e);
            return RefundResultVo.ChannelRefundResult.builder()
                    .channel(channel).refundAmount(BigDecimal.ZERO)
                    .success(false).message("退款到余额失败：" + e.getMessage()).build();
        }
    }

    private RefundResultVo.ChannelRefundResult refundToOriginalChannel(
            Order order, String virtualOrderId, BigDecimal refundAmount, String channel) {
        try {
            RefundPayOrderRequest refundRequest = new RefundPayOrderRequest();
            refundRequest.setOutTradeNo(virtualOrderId);
            refundRequest.setMoney(refundAmount);
            Result<?> result = refundOrder(refundRequest);
            if (result.getCode() == 200) {
                return RefundResultVo.ChannelRefundResult.builder()
                        .channel(channel).virtualOrderId(virtualOrderId).refundAmount(refundAmount)
                        .success(true).message("原路退款成功").build();
            } else {
                return RefundResultVo.ChannelRefundResult.builder()
                        .channel(channel).virtualOrderId(virtualOrderId).refundAmount(BigDecimal.ZERO)
                        .success(false).message("原路退款失败：" + result.getMessage()).build();
            }
        } catch (Exception e) {
            LogEsUtil.error("原路退款失败，订单id：" + order.getId(), e);
            return RefundResultVo.ChannelRefundResult.builder()
                    .channel(channel).virtualOrderId(virtualOrderId).refundAmount(BigDecimal.ZERO)
                    .success(false).message("原路退款失败：" + e.getMessage()).build();
        }
    }

    private RefundResultVo.ChannelRefundResult closeUnpaidOrder(String virtualOrderId, String channel) {
        try {
            RefundPayOrderRequest refundRequest = new RefundPayOrderRequest();
            refundRequest.setOutTradeNo(virtualOrderId);
            refundRequest.setMoney(BigDecimal.ZERO);
            Result<?> result = refundOrder(refundRequest);
            if (result.getCode() == 200) {
                return RefundResultVo.ChannelRefundResult.builder()
                        .channel(channel).virtualOrderId(virtualOrderId).refundAmount(BigDecimal.ZERO)
                        .success(true).message("未支付订单已关闭").build();
            } else {
                return RefundResultVo.ChannelRefundResult.builder()
                        .channel(channel).virtualOrderId(virtualOrderId).refundAmount(BigDecimal.ZERO)
                        .success(true).message("订单已关闭或不存在").build();
            }
        } catch (Exception e) {
            LogEsUtil.warn("关闭未支付订单异常，虚拟订单id：" + virtualOrderId);
            return RefundResultVo.ChannelRefundResult.builder()
                    .channel(channel).virtualOrderId(virtualOrderId).refundAmount(BigDecimal.ZERO)
                    .success(true).message("订单关闭异常，但可继续处理").build();
        }
    }
}
