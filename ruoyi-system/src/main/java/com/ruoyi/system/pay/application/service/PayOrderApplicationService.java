package com.ruoyi.system.pay.application.service;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.system.constant.BalanceDetailStatus;
import com.ruoyi.system.constant.OrderStatus;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.entity.OrderPayType;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.OrderMapper;
import com.ruoyi.system.pay.application.dto.request.*;
import com.ruoyi.system.pay.application.vo.*;
import com.ruoyi.system.pay.domain.service.PayDomainService;
import com.ruoyi.system.pay.domain.valueobject.RefundMode;
import com.ruoyi.system.pay.infrastructure.client.PayPlatformClient;
import com.ruoyi.system.pay.infrastructure.config.PayProperties;
import com.ruoyi.system.service.IOrderPayTypeService;
import com.ruoyi.system.service.IWalletBalanceService;
import com.ruoyi.system.util.LogEsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [支付订单应用服务]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Slf4j
@Service
public class PayOrderApplicationService {

    @Resource
    private PayPlatformClient payPlatformClient;

    @Resource
    private PayProperties payProperties;

    @Resource
    private PayDomainService payDomainService;

    @Resource
    private HttpServletRequest httpServletRequest;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private IOrderPayTypeService orderPayTypeService;

    @Resource
    private IWalletBalanceService walletBalanceService;

    /**
     * 统一下单（API支付）
     */
    public CreatePayOrderVo createOrder(CreatePayOrderRequest request) {

        // 构建请求参数
        Map<String, String> params = new HashMap<>();
        params.put("pid", String.valueOf(payProperties.getPid()));
        params.put("method", "jump");
        params.put("type", request.getType());
        params.put("out_trade_no", request.getVirtualOutTradeNo());
        params.put("name", request.getName());
        params.put("money", request.getMoney().toString());
        params.put("clientip", request.getClientIp());
        params.put("notify_url", payProperties.getNotifyUrl());
        params.put("return_url", payProperties.getReturnUrl()+request.getRealOutTradeNo());

        // 调用支付平台
        Map<String, Object> response = payPlatformClient.createOrder(params);

        // 验签
        boolean verified = payPlatformClient.verifyResponse(response);
        if (!verified) {
            LogEsUtil.warn("支付平台响应验签失败，响应数据：" + response);
            return null;
        }

        // 转换为VO
        CreatePayOrderVo vo = new CreatePayOrderVo();
        vo.setCode((Integer) response.get("code"));
        vo.setTradeNo((String) response.get("trade_no"));
        vo.setPayUrl((String) response.get("pay_info"));
        return vo;
    }

    /**
     * 页面跳转支付
     */
    public Result<String> pagePay(PagePayRequest request) {
        // 构建请求参数
        Map<String, String> params = new HashMap<>();
        if (request.getPid() != null) {
            params.put("pid", String.valueOf(request.getPid()));
        }
        if (request.getType() != null) {
            params.put("type", request.getType());
        }
        params.put("out_trade_no", request.getOutTradeNo());
        params.put("name", request.getName());
        params.put("money", request.getMoney());

        if (request.getNotifyUrl() != null) {
            params.put("notify_url", request.getNotifyUrl());
        } else {
            params.put("notify_url", payProperties.getNotifyUrl());
        }

        if (request.getReturnUrl() != null) {
            params.put("return_url", request.getReturnUrl());
        } else {
            params.put("return_url", payProperties.getReturnUrl());
        }

        if (request.getParam() != null) {
            params.put("param", request.getParam());
        }
        if (request.getChannelId() != null) {
            params.put("channel_id", String.valueOf(request.getChannelId()));
        }

        // 返回跳转URL
        String payUrl = payPlatformClient.submitOrder(params);
        return Result.success(payUrl);
    }

    /**
     * 查询订单
     */
    public Result<QueryPayOrderVo> queryOrder(QueryPayOrderRequest request) {
        Map<String, String> params = new HashMap<>();
        if (request.getPid() != null) {
            params.put("pid", String.valueOf(request.getPid()));
        }
        if (request.getTradeNo() != null) {
            params.put("trade_no", request.getTradeNo());
        }
        if (request.getOutTradeNo() != null) {
            params.put("out_trade_no", request.getOutTradeNo());
        }

        Map<String, Object> response = payPlatformClient.queryOrder(params);

        QueryPayOrderVo vo = new QueryPayOrderVo();
        vo.setCode((Integer) response.get("code"));
        vo.setMsg((String) response.get("msg"));
        vo.setTradeNo((String) response.get("trade_no"));
        vo.setOutTradeNo((String) response.get("out_trade_no"));
        vo.setApiTradeNo((String) response.get("api_trade_no"));
        vo.setType((String) response.get("type"));
        vo.setPid(Integer.valueOf((String) response.get("pid")));
        vo.setAddTime((String) response.get("addtime"));
        vo.setEndTime((String) response.get("endtime"));
        vo.setName((String) response.get("name"));
        vo.setMoney((String) response.get("money"));
        vo.setStatus(Integer.valueOf((String) response.get("status")));
        vo.setParam((String) response.get("param"));
        vo.setBuyer((String) response.get("buyer"));

        return Result.success(vo);
    }

    /**
     * 订单退款
     */
    public Result<?> refundOrder(RefundPayOrderRequest request) {
        Map<String, String> params = new HashMap<>();
        if (request.getPid() != null) {
            params.put("pid", String.valueOf(request.getPid()));
        }
        if (request.getTradeNo() != null) {
            params.put("trade_no", request.getTradeNo());
        }
        if (request.getOutTradeNo() != null) {
            params.put("out_trade_no", request.getOutTradeNo());
        }
        params.put("money", request.getMoney().toString());

        Map<String, Object> response = payPlatformClient.refundOrder(params);

        Integer code = (Integer) response.get("code");
        if (code != null && code == 1) {
            return Result.success("退款申请成功");
        } else {
            String msg = (String) response.get("msg");
            return Result.fail(msg != null ? msg : "退款申请失败");
        }
    }

    /**
     * 为订单创建支付并获取支付二维码/链接
     * 供 IOrderService 在获取二维码时调用
     *
     * @param order    订单对象
     * @param payType  支付方式：alipay/wxpay/qqpay
     * @return 支付二维码链接或跳转URL
     */
    public CreatePayOrderVo createPayForOrder(Order order, String payType) {
        return createPayForOrderWithVirtualId(order, payType, order.getId());
    }

    /**
     * 为订单创建支付并获取支付二维码/链接（使用虚拟订单号）
     * 供 IOrderService 在获取二维码时调用
     *
     * @param order           订单对象
     * @param payType         支付方式：alipay/wxpay/qqpay
     * @param virtualOrderId  虚拟订单号
     * @return 支付二维码链接或跳转URL
     */
    public CreatePayOrderVo createPayForOrderWithVirtualId(Order order, String payType, String virtualOrderId) {
        // 构建统一下单请求
        CreatePayOrderRequest request = new CreatePayOrderRequest();
        request.setType(payType);
        request.setVirtualOutTradeNo(virtualOrderId);
        request.setRealOutTradeNo(order.getId());
        request.setName(order.getDescription() != null ? order.getDescription() : "订单支付");
        request.setMoney(order.getAmount());
        request.setClientIp(getClientIp());

        // 调用统一下单接口
        return createOrder(request);
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp() {
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
        // 多个代理情况，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 根据订单ID执行退款
     *
     * @param request 退款请求
     * @return 退款结果
     */
    public RefundResultVo refundByOrder(OrderRefundRequest request) {
        String orderId = request.getOrderId();
        RefundMode refundMode = RefundMode.fromCode(request.getRefundMode());
        Boolean refundToBalance = request.getRefundToBalance();

        // 1. 查询订单
        Order order = orderMapper.queryByIdForUpdate(orderId);
        if (order == null) {
            return RefundResultVo.builder()
                    .success(false)
                    .message("订单不存在")
                    .build();
        }

        // 2. 查询订单支付方式记录
        OrderPayType orderPayType = orderPayTypeService.getByOrderId(orderId);

        // 3. 获取支付方式
        String paymentType = order.getPaymentType();
        BigDecimal orderAmount = order.getAmount();

        // 4. 计算退款金额
        BigDecimal refundAmount = request.getRefundAmount();
        if (refundAmount == null) {
            refundAmount = orderAmount;
        }
        if (refundAmount.compareTo(orderAmount) > 0){
            return RefundResultVo.builder()
                    .success(false)
                    .message("退款金额大于支付金额")
                    .build();
        }

        // 5. 根据退款模式执行退款
        List<RefundResultVo.ChannelRefundResult> results = new ArrayList<>();

        switch (refundMode) {
            case USER_REFUND:
            case SYSTEM_TIMEOUT:
                // 用户/超时退款：退所有已支付的渠道
                results = refundAllChannels(order, orderPayType, paymentType,
                        refundAmount, refundToBalance);
                break;
            case ERROR_REFUND:
                // 差错退款：只退非支付方式的渠道
                results = refundErrorChannels(order, orderPayType, paymentType,
                        refundAmount, refundToBalance);
                break;
        }

        // 6. 计算总退款金额
        BigDecimal totalRefund = BigDecimal.ZERO;
        for (RefundResultVo.ChannelRefundResult result : results) {
            if (result.getSuccess() && result.getRefundAmount() != null) {
                totalRefund = totalRefund.add(result.getRefundAmount());
            }
        }

        // 7. 构建返回结果
        boolean allSuccess = results.stream().allMatch(RefundResultVo.ChannelRefundResult::getSuccess);

        return RefundResultVo.builder()
                .success(allSuccess)
                .totalRefundAmount(totalRefund)
                .channelResults(results)
                .message(allSuccess ? "退款成功" : "部分渠道退款失败")
                .build();
    }

    /**
     * 退所有已支付的渠道（用户/超时退款）
     */
    private List<RefundResultVo.ChannelRefundResult> refundAllChannels(
            Order order, OrderPayType orderPayType, String paymentType,
            BigDecimal refundAmount, Boolean refundToBalance) {

        List<RefundResultVo.ChannelRefundResult> results = new ArrayList<>();

        // 1. 处理微信支付
        if (orderPayType != null && StrUtil.isNotBlank(orderPayType.getWxOrderId())) {
            RefundResultVo.ChannelRefundResult wxResult = refundWechat(
                    order, orderPayType.getWxOrderId(), paymentType,
                    refundAmount, refundToBalance);
            results.add(wxResult);
        }

        // 2. 处理支付宝支付
        if (orderPayType != null && StrUtil.isNotBlank(orderPayType.getAlipayOrderId())) {
            RefundResultVo.ChannelRefundResult aliResult = refundAlipay(
                    order, orderPayType.getAlipayOrderId(), paymentType,
                    refundAmount, refundToBalance);
            results.add(aliResult);
        }

        // 3. 处理余额支付（如果订单支付方式是余额）
        if (OrderStatus.BALANCE_PAY.equals(paymentType)) {
            RefundResultVo.ChannelRefundResult balanceResult = refundBalance(
                    order, refundAmount);
            results.add(balanceResult);
        }

        return results;
    }

    /**
     * 差错退款：只退非支付方式的渠道
     */
    private List<RefundResultVo.ChannelRefundResult> refundErrorChannels(
            Order order, OrderPayType orderPayType, String paymentType,
            BigDecimal refundAmount, Boolean refundToBalance) {

        List<RefundResultVo.ChannelRefundResult> results = new ArrayList<>();

        // 1. 处理微信支付，如果支付状态不为空，查询过微信支付方式，且支付方式不为微信
        if (orderPayType != null && StrUtil.isNotBlank(orderPayType.getWxOrderId())
                && !OrderStatus.WECHAT_PAY.equals(paymentType)) {
            // 差错退款强制退到余额
            RefundResultVo.ChannelRefundResult wxResult = refundWechat(
                    order, orderPayType.getWxOrderId(), paymentType,
                    refundAmount, false);
            results.add(wxResult);
        }

        // 2. 处理支付宝支付（如果支付方式不是支付宝）
        if (orderPayType != null && StrUtil.isNotBlank(orderPayType.getAlipayOrderId())
                && !OrderStatus.ALIPAY_PAY.equals(paymentType)) {
            // 差错退款强制退到余额
            RefundResultVo.ChannelRefundResult aliResult = refundAlipay(
                    order, orderPayType.getAlipayOrderId(), paymentType,
                    refundAmount, false);
            results.add(aliResult);
        }

        return results;
    }

    /**
     * 微信退款
     */
    private RefundResultVo.ChannelRefundResult refundWechat(
            Order order, String virtualOrderId, String paymentType,
            BigDecimal refundAmount, Boolean refundToBalance) {

        // 1. 先查询订单状态
        QueryPayOrderRequest queryRequest = new QueryPayOrderRequest();
        queryRequest.setOutTradeNo(virtualOrderId);
        Result<QueryPayOrderVo> queryResult = queryOrder(queryRequest);

        if (queryResult.getCode() != 200 || queryResult.getData() == null) {
            return RefundResultVo.ChannelRefundResult.builder()
                    .channel(OrderStatus.WECHAT_PAY)
                    .virtualOrderId(virtualOrderId)
                    .refundAmount(BigDecimal.ZERO)
                    .success(false)
                    .message("查询订单状态失败")
                    .build();
        }

        QueryPayOrderVo orderVo = queryResult.getData();

        // 2. 如果未支付，调用退款接口（金额0）关闭订单，防止后续支付
        if (orderVo.getStatus() == null || orderVo.getStatus() != 1) {
            return closeUnpaidOrder(virtualOrderId, OrderStatus.WECHAT_PAY);
        }

        // 3. 计算实际退款金额
        BigDecimal actualRefundAmount;
        if (OrderStatus.WECHAT_PAY.equals(paymentType)) {
            // 支付渠道需要扣手续费
            if (refundToBalance) {
                // 退到余额，不扣手续费
                actualRefundAmount = refundAmount;
            } else {
                // 原路退回，扣手续费
                BigDecimal feeRate = payProperties.getWechatFeeRate();
                actualRefundAmount = refundAmount.multiply(
                        BigDecimal.ONE.subtract(feeRate));
            }
        } else {
            // 非支付渠道全额退款
            actualRefundAmount = refundAmount;
        }

        // 4. 执行退款
        if (refundToBalance && OrderStatus.WECHAT_PAY.equals(paymentType)) {
            // 退到余额
            return refundToBalance(order, actualRefundAmount, OrderStatus.WECHAT_PAY);
        } else {
            // 原路退回
            return refundToOriginalChannel(order, virtualOrderId, actualRefundAmount,
                    OrderStatus.WECHAT_PAY);
        }
    }

    /**
     * 支付宝退款
     */
    private RefundResultVo.ChannelRefundResult refundAlipay(
            Order order, String virtualOrderId, String paymentType,
            BigDecimal refundAmount, Boolean refundToBalance) {

        // 类似微信退款的逻辑
        QueryPayOrderRequest queryRequest = new QueryPayOrderRequest();
        queryRequest.setOutTradeNo(virtualOrderId);
        Result<QueryPayOrderVo> queryResult = queryOrder(queryRequest);

        if (queryResult.getCode() != 200 || queryResult.getData() == null) {
            return RefundResultVo.ChannelRefundResult.builder()
                    .channel(OrderStatus.ALIPAY_PAY)
                    .virtualOrderId(virtualOrderId)
                    .refundAmount(BigDecimal.ZERO)
                    .success(false)
                    .message("查询订单状态失败")
                    .build();
        }

        QueryPayOrderVo orderVo = queryResult.getData();

        // 如果未支付，调用退款接口（金额0）关闭订单，防止后续支付
        if (orderVo.getStatus() == null || orderVo.getStatus() != 1) {
            return closeUnpaidOrder(virtualOrderId, OrderStatus.ALIPAY_PAY);
        }

        BigDecimal actualRefundAmount;
        if (OrderStatus.ALIPAY_PAY.equals(paymentType)) {
            if (refundToBalance) {
                actualRefundAmount = refundAmount;
            } else {
                BigDecimal feeRate = payProperties.getAlipayFeeRate();
                actualRefundAmount = refundAmount.multiply(
                        BigDecimal.ONE.subtract(feeRate));
            }
        } else {
            actualRefundAmount = refundAmount;
        }

        if (refundToBalance && OrderStatus.ALIPAY_PAY.equals(paymentType)) {
            return refundToBalance(order, actualRefundAmount, OrderStatus.ALIPAY_PAY);
        } else {
            return refundToOriginalChannel(order, virtualOrderId, actualRefundAmount,
                    OrderStatus.ALIPAY_PAY);
        }
    }

    /**
     * 余额退款
     */
    private RefundResultVo.ChannelRefundResult refundBalance(
            Order order, BigDecimal refundAmount) {

        try {
            // 增加用户余额（使用通用方法）
            walletBalanceService.addBalance(refundAmount, BalanceDetailStatus.REFUND,order.getUserId());

            return RefundResultVo.ChannelRefundResult.builder()
                    .channel(OrderStatus.BALANCE_PAY)
                    .refundAmount(refundAmount)
                    .success(true)
                    .message("余额退款成功")
                    .build();
        } catch (Exception e) {
            LogEsUtil.error("余额退款失败，订单id：" + order.getId(), e);
            return RefundResultVo.ChannelRefundResult.builder()
                    .channel(OrderStatus.BALANCE_PAY)
                    .refundAmount(BigDecimal.ZERO)
                    .success(false)
                    .message("余额退款失败：" + e.getMessage())
                    .build();
        }
    }

    /**
     * 退款到余额
     */
    private RefundResultVo.ChannelRefundResult refundToBalance(
            Order order, BigDecimal refundAmount, String channel) {

        try {
            walletBalanceService.addBalance(refundAmount, BalanceDetailStatus.REFUND,order.getUserId());

            return RefundResultVo.ChannelRefundResult.builder()
                    .channel(channel)
                    .refundAmount(refundAmount)
                    .success(true)
                    .message("退款到余额成功")
                    .build();
        } catch (Exception e) {
            LogEsUtil.error("退款到余额失败，订单id：" + order.getId(), e);
            return RefundResultVo.ChannelRefundResult.builder()
                    .channel(channel)
                    .refundAmount(BigDecimal.ZERO)
                    .success(false)
                    .message("退款到余额失败：" + e.getMessage())
                    .build();
        }
    }

    /**
     * 原路退回
     */
    private RefundResultVo.ChannelRefundResult refundToOriginalChannel(
            Order order, String virtualOrderId, BigDecimal refundAmount, String channel) {

        try {
            RefundPayOrderRequest refundRequest = new RefundPayOrderRequest();
            refundRequest.setOutTradeNo(virtualOrderId);
            refundRequest.setMoney(refundAmount);

            Result<?> result = refundOrder(refundRequest);

            if (result.getCode() == 200) {
                return RefundResultVo.ChannelRefundResult.builder()
                        .channel(channel)
                        .virtualOrderId(virtualOrderId)
                        .refundAmount(refundAmount)
                        .success(true)
                        .message("原路退款成功")
                        .build();
            } else {
                return RefundResultVo.ChannelRefundResult.builder()
                        .channel(channel)
                        .virtualOrderId(virtualOrderId)
                        .refundAmount(BigDecimal.ZERO)
                        .success(false)
                        .message("原路退款失败：" + result.getMessage())
                        .build();
            }
        } catch (Exception e) {
            LogEsUtil.error("原路退款失败，订单id：" + order.getId(), e);
            return RefundResultVo.ChannelRefundResult.builder()
                    .channel(channel)
                    .virtualOrderId(virtualOrderId)
                    .refundAmount(BigDecimal.ZERO)
                    .success(false)
                    .message("原路退款失败：" + e.getMessage())
                    .build();
        }
    }

    /**
     * 关闭未支付订单（退款金额0，用于关单防止后续支付）
     */
    private RefundResultVo.ChannelRefundResult closeUnpaidOrder(
            String virtualOrderId, String channel) {

        try {
            // 调用退款接口，金额设为0，实现关单
            RefundPayOrderRequest refundRequest = new RefundPayOrderRequest();
            refundRequest.setOutTradeNo(virtualOrderId);
            refundRequest.setMoney(BigDecimal.ZERO);

            Result<?> result = refundOrder(refundRequest);

            if (result.getCode() == 200) {
                return RefundResultVo.ChannelRefundResult.builder()
                        .channel(channel)
                        .virtualOrderId(virtualOrderId)
                        .refundAmount(BigDecimal.ZERO)
                        .success(true)
                        .message("未支付订单已关闭")
                        .build();
            } else {
                // 关单失败（可能订单已经关闭或不存在）
                return RefundResultVo.ChannelRefundResult.builder()
                        .channel(channel)
                        .virtualOrderId(virtualOrderId)
                        .refundAmount(BigDecimal.ZERO)
                        .success(true) // 视为成功，因为订单已经不能支付了
                        .message("订单已关闭或不存在")
                        .build();
            }
        } catch (Exception e) {
            LogEsUtil.warn("关闭未支付订单异常，虚拟订单id：" + virtualOrderId + "，异常：" + e.getMessage());
            // 异常时也视为成功，避免阻塞整个退款流程
            return RefundResultVo.ChannelRefundResult.builder()
                    .channel(channel)
                    .virtualOrderId(virtualOrderId)
                    .refundAmount(BigDecimal.ZERO)
                    .success(true)
                    .message("订单关闭异常，但可继续处理")
                    .build();
        }
    }
}
