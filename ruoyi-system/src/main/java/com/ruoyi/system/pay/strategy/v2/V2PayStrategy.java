package com.ruoyi.system.pay.strategy.v2;

import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.pay.application.dto.request.*;
import com.ruoyi.system.pay.application.vo.CreatePayOrderVo;
import com.ruoyi.system.pay.application.vo.QueryPayOrderVo;
import com.ruoyi.system.pay.strategy.AbstractPayStrategy;
import com.ruoyi.system.util.LogEsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * [V2支付策略实现 - 佳梦云付]
 * RSA-SHA256签名，/api/pay/create, /api/pay/submit, /api/pay/query, /api/pay/refund
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/4/6
 */
@Slf4j
@Component("v2PayStrategy")
@ConditionalOnProperty(name = "pay.platform.version", havingValue = "v2", matchIfMissing = true)
public class V2PayStrategy extends AbstractPayStrategy {

    @Resource
    private V2PayPlatformClient v2PayPlatformClient;
    @Resource
    private V2PayProperties v2PayProperties;

    @Override
    protected BigDecimal getWechatFeeRate() {
        return v2PayProperties.getWechatFeeRate();
    }

    @Override
    protected BigDecimal getAlipayFeeRate() {
        return v2PayProperties.getAlipayFeeRate();
    }

    @Override
    public CreatePayOrderVo createOrder(CreatePayOrderRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("pid", String.valueOf(v2PayProperties.getPid()));
        params.put("method", "jump");
        params.put("type", request.getType());
        params.put("out_trade_no", request.getVirtualOutTradeNo());
        params.put("name", request.getName());
        params.put("money", request.getMoney().toString());
        params.put("clientip", request.getClientIp());
        params.put("notify_url", v2PayProperties.getNotifyUrl());
        params.put("return_url", v2PayProperties.getReturnUrl() + request.getRealOutTradeNo());

        Map<String, Object> response = v2PayPlatformClient.createOrder(params);
        boolean verified = v2PayPlatformClient.verifyResponse(response);
        if (!verified) {
            LogEsUtil.warn("V2支付平台响应验签失败，响应数据：" + response);
            return null;
        }

        CreatePayOrderVo vo = new CreatePayOrderVo();
        vo.setCode((Integer) response.get("code"));
        vo.setTradeNo((String) response.get("trade_no"));
        vo.setPayUrl((String) response.get("pay_info"));
        return vo;
    }

    @Override
    public Result<String> pagePay(PagePayRequest request) {
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
        params.put("notify_url", request.getNotifyUrl() != null ? request.getNotifyUrl() : v2PayProperties.getNotifyUrl());
        params.put("return_url", request.getReturnUrl() != null ? request.getReturnUrl() : v2PayProperties.getReturnUrl());
        if (request.getParam() != null) {
            params.put("param", request.getParam());
        }
        if (request.getChannelId() != null) {
            params.put("channel_id", String.valueOf(request.getChannelId()));
        }
        String payUrl = v2PayPlatformClient.submitOrder(params);
        return Result.success(payUrl);
    }

    @Override
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

        Map<String, Object> response = v2PayPlatformClient.queryOrder(params);
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

    @Override
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

        Map<String, Object> response = v2PayPlatformClient.refundOrder(params);
        Integer code = (Integer) response.get("code");
        if (code != null && code == 1) {
            return Result.success("退款申请成功");
        } else {
            String msg = (String) response.get("msg");
            return Result.fail(msg != null ? msg : "退款申请失败");
        }
    }

    @Override
    public CreatePayOrderVo createPayForOrderWithVirtualId(Order order, String payType, String virtualOrderId) {
        CreatePayOrderRequest request = new CreatePayOrderRequest();
        request.setType(payType);
        request.setVirtualOutTradeNo(virtualOrderId);
        request.setRealOutTradeNo(order.getId());
        request.setName(order.getDescription() != null ? order.getDescription() : "订单支付");
        request.setMoney(order.getAmount());
        request.setClientIp(getClientIp());
        return createOrder(request);
    }

    @Override
    public boolean verifyNotifySign(Map<String, String> params) {
        Map<String, Object> responseMap = new HashMap<>(params);
        return v2PayPlatformClient.verifyResponse(responseMap);
    }
}
