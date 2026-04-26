package com.ruoyi.system.pay.strategy.v2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.system.util.LogEsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * [V2支付平台HTTP客户端 - 佳梦云付]
 * API路径：/api/pay/create, /api/pay/submit, /api/pay/query, /api/pay/refund
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/4/6
 */
@Slf4j
@Component("v2PayPlatformClient")
@ConditionalOnProperty(name = "pay.platform.version", havingValue = "v2", matchIfMissing = true)
public class V2PayPlatformClient {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private V2PayProperties v2PayProperties;
    @Resource
    private V2SignUtil v2SignUtil;

    private static final String CREATE_ORDER_URL = "/api/pay/create";
    private static final String SUBMIT_ORDER_URL = "/api/pay/submit";
    private static final String QUERY_ORDER_URL = "/api/pay/query";
    private static final String REFUND_URL = "/api/pay/refund";
    private static final String TRANSFER_URL = "/api/transfer/submit";

    public Map<String, Object> createOrder(Map<String, String> params) {
        return post(CREATE_ORDER_URL, params);
    }

    public String submitOrder(Map<String, String> params) {
        String url = v2PayProperties.getBaseUrl() + SUBMIT_ORDER_URL;
        Map<String, String> signedParams = addSign(params);
        StringBuilder formUrl = new StringBuilder(url).append("?");
        signedParams.forEach((k, v) -> formUrl.append(k).append("=").append(v).append("&"));
        return formUrl.toString();
    }

    public Map<String, Object> queryOrder(Map<String, String> params) {
        return post(QUERY_ORDER_URL, params);
    }

    public Map<String, Object> refundOrder(Map<String, String> params) {
        return post(REFUND_URL, params);
    }

    public Map<String, Object> transfer(Map<String, String> params) {
        return post(TRANSFER_URL, params);
    }

    private Map<String, Object> post(String uri, Map<String, String> params) {
        String url = v2PayProperties.getBaseUrl() + uri;
        Map<String, String> signedParams = addSign(params);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        signedParams.forEach(map::add);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            LogEsUtil.warn("V2请求支付平台失败，HTTP状态码：" + response.getStatusCode());
            throw new RuntimeException("请求支付平台失败");
        }
        return JSON.parseObject(response.getBody(), new TypeReference<Map<String, Object>>() {});
    }

    private Map<String, String> addSign(Map<String, String> params) {
        Map<String, String> newParams = new HashMap<>(params);
        if (!newParams.containsKey("pid")) {
            newParams.put("pid", String.valueOf(v2PayProperties.getPid()));
        }
        if (!newParams.containsKey("timestamp")) {
            newParams.put("timestamp", v2SignUtil.getTimestamp());
        }
        if (!newParams.containsKey("sign_type")) {
            newParams.put("sign_type", v2PayProperties.getSignType());
        }
        String sign = v2SignUtil.generateSign(newParams, v2PayProperties.getMerchantPrivateKey());
        newParams.put("sign", sign);
        return newParams;
    }

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
        return v2SignUtil.verifySign(params, v2PayProperties.getPlatformPublicKey(), sign);
    }
}
