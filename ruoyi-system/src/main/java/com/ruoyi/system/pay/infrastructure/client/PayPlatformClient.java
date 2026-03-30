package com.ruoyi.system.pay.infrastructure.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.system.pay.infrastructure.config.PayProperties;
import com.ruoyi.system.pay.infrastructure.util.SignUtil;
import com.ruoyi.system.util.LogEsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * [支付平台HTTP客户端]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Slf4j
@Component
public class PayPlatformClient {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private PayProperties payProperties;

    @Resource
    private SignUtil signUtil;

    private static final String CREATE_ORDER_URL = "/api/pay/create";
    private static final String SUBMIT_ORDER_URL = "/api/pay/submit";
    private static final String QUERY_ORDER_URL = "/api/pay/query";
    private static final String REFUND_URL = "/api/pay/refund";
    private static final String TRANSFER_URL = "/api/transfer/submit";

    /**
     * 统一下单（API支付）
     */
    public Map<String, Object> createOrder(Map<String, String> params) {
        return post(CREATE_ORDER_URL, params);
    }

    /**
     * 页面跳转支付
     */
    public String submitOrder(Map<String, String> params) {
        String url = payProperties.getBaseUrl() + SUBMIT_ORDER_URL;
        Map<String, String> signedParams = addSign(params);

        // 构建表单提交的URL
        StringBuilder formUrl = new StringBuilder(url).append("?");
        signedParams.forEach((k, v) -> formUrl.append(k).append("=").append(v).append("&"));

        return formUrl.toString();
    }

    /**
     * 查询订单
     */
    public Map<String, Object> queryOrder(Map<String, String> params) {
        return post(QUERY_ORDER_URL, params);
    }

    /**
     * 订单退款
     */
    public Map<String, Object> refundOrder(Map<String, String> params) {
        return post(REFUND_URL, params);
    }

    /**
     * 订单退款
     */
    public Map<String, Object> transfer(Map<String, String> params) {
        return post(TRANSFER_URL, params);
    }


    /**
     * 发送POST请求
     */
    private Map<String, Object> post(String uri, Map<String, String> params) {
        String url = payProperties.getBaseUrl() + uri;
        Map<String, String> signedParams = addSign(params);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        signedParams.forEach(map::add);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);


        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            LogEsUtil.warn("请求支付平台失败，HTTP状态码：" + response.getStatusCode());
            throw new RuntimeException("请求支付平台失败");
        }

        String body = response.getBody();


        return JSON.parseObject(body, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * 添加签名参数
     */
    private Map<String, String> addSign(Map<String, String> params) {
        Map<String, String> newParams = new HashMap<>(params);

        // 添加必要参数
        if (!newParams.containsKey("pid")) {
            newParams.put("pid", String.valueOf(payProperties.getPid()));
        }
        if (!newParams.containsKey("timestamp")) {
            newParams.put("timestamp", signUtil.getTimestamp());
        }
        if (!newParams.containsKey("sign_type")) {
            newParams.put("sign_type", payProperties.getSignType());
        }

        // 生成签名
        String sign = signUtil.generateSign(newParams, payProperties.getMerchantPrivateKey());
        newParams.put("sign", sign);

        return newParams;
    }

    /**
     * 验证响应签名
     */
    public boolean verifyResponse(Map<String, Object> response) {
        Map<String, String> params = new HashMap<>();
        response.forEach((k, v) -> {
            if (v != null) {
                params.put(k, v.toString());
            }
        });

        String sign = params.remove("sign");
        if (sign == null || sign.isEmpty()) {
            return false;
        }

        return signUtil.verifySign(params, payProperties.getPlatformPublicKey(), sign);
    }
}
