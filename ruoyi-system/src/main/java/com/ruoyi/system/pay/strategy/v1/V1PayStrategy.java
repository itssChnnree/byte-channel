package com.ruoyi.system.pay.strategy.v1;

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
 * [V1支付策略实现 - 小白云OS]
 * MD5签名，/mapi.php, /submit.php, /api.php?act=order/refund
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/4/6
 */
@Slf4j
@Component("v1PayStrategy")
@ConditionalOnProperty(name = "pay.platform.version", havingValue = "v1")
public class V1PayStrategy extends AbstractPayStrategy {

    @Resource
    private V1PayPlatformClient v1PayPlatformClient;
    @Resource
    private V1PayProperties v1PayProperties;

    @Override
    protected BigDecimal getWechatFeeRate() {
        return v1PayProperties.getWechatFeeRate();
    }

    @Override
    protected BigDecimal getAlipayFeeRate() {
        return v1PayProperties.getAlipayFeeRate();
    }

    @Override
    public CreatePayOrderVo createOrder(CreatePayOrderRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("pid", String.valueOf(v1PayProperties.getPid()));
        params.put("type", request.getType());
        params.put("out_trade_no", request.getVirtualOutTradeNo());
        params.put("name", request.getName());
        params.put("money", request.getMoney().toString());
        params.put("clientip", request.getClientIp());
        params.put("notify_url", v1PayProperties.getNotifyUrl());
        params.put("return_url", v1PayProperties.getReturnUrl() + request.getRealOutTradeNo());

        Map<String, Object> response = v1PayPlatformClient.createOrder(params);

        // V1成功码为 code=1，返回 payurl/qrcode/urlscheme
        Integer code = (Integer) response.get("code");
        if (code == null || code != 1) {
            String msg = (String) response.get("msg");
            LogEsUtil.warn("V1统一下单失败：" + msg);
            return null;
        }

        CreatePayOrderVo vo = new CreatePayOrderVo();
        vo.setCode(code);
        vo.setTradeNo((String) response.get("trade_no"));
        // V1返回 payurl/qrcode/urlscheme，统一映射到 payUrl
        String payUrl = (String) response.getOrDefault("payurl",
                response.getOrDefault("qrcode",
                        response.getOrDefault("urlscheme", "")));
        vo.setPayUrl(payUrl);
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
        params.put("notify_url", request.getNotifyUrl() != null ? request.getNotifyUrl() : v1PayProperties.getNotifyUrl());
        params.put("return_url", request.getReturnUrl() != null ? request.getReturnUrl() : v1PayProperties.getReturnUrl());
        if (request.getParam() != null) {
            params.put("param", request.getParam());
        }
        String payUrl = v1PayPlatformClient.submitOrder(params);
        return Result.success(payUrl);
    }

    @Override
    public Result<QueryPayOrderVo> queryOrder(QueryPayOrderRequest request) {
        Map<String, String> params = new HashMap<>();
        if (request.getTradeNo() != null) {
            params.put("trade_no", request.getTradeNo());
        }
        if (request.getOutTradeNo() != null) {
            params.put("out_trade_no", request.getOutTradeNo());
        }

        Map<String, Object> response = v1PayPlatformClient.queryOrder(params);
        Integer code = (Integer) response.get("code");
        if (code == null || code != 1) {
            return Result.fail("查询订单失败：" + response.get("msg"));
        }

        QueryPayOrderVo vo = new QueryPayOrderVo();
        vo.setCode(code);
        vo.setMsg((String) response.get("msg"));
        vo.setTradeNo((String) response.get("trade_no"));
        vo.setOutTradeNo((String) response.get("out_trade_no"));
        vo.setApiTradeNo((String) response.get("api_trade_no"));
        vo.setType((String) response.get("type"));
        // V1的pid可能返回Integer或String
        Object pidObj = response.get("pid");
        if (pidObj instanceof Integer) {
            vo.setPid((Integer) pidObj);
        } else if (pidObj instanceof String) {
            vo.setPid(Integer.valueOf((String) pidObj));
        }
        vo.setAddTime((String) response.get("addtime"));
        vo.setEndTime((String) response.get("endtime"));
        vo.setName((String) response.get("name"));
        vo.setMoney((String) response.get("money"));
        Object statusObj = response.get("status");
        if (statusObj instanceof Integer) {
            vo.setStatus((Integer) statusObj);
        } else if (statusObj instanceof String) {
            vo.setStatus(Integer.valueOf((String) statusObj));
        }
        vo.setParam((String) response.get("param"));
        vo.setBuyer((String) response.get("buyer"));
        return Result.success(vo);
    }

    @Override
    public Result<?> refundOrder(RefundPayOrderRequest request) {
        Map<String, String> params = new HashMap<>();
        if (request.getTradeNo() != null) {
            params.put("trade_no", request.getTradeNo());
        }
        if (request.getOutTradeNo() != null) {
            params.put("out_trade_no", request.getOutTradeNo());
        }
        params.put("money", request.getMoney().toString());

        Map<String, Object> response = v1PayPlatformClient.refundOrder(params);
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
        return v1PayPlatformClient.verifyResponse(params);
    }
}
