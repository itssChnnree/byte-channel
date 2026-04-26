package com.ruoyi.system.pay.strategy.v1;

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
 * [V1支付平台HTTP客户端 - 小白云OS]
 * API路径：/mapi.php（需签名）, /submit.php（需签名）, /api.php（不签名，用pid+key认证）
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/4/6
 */
@Slf4j
@Component("v1PayPlatformClient")
@ConditionalOnProperty(name = "pay.platform.version", havingValue = "v1")
public class V1PayPlatformClient {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private V1PayProperties v1PayProperties;
    @Resource
    private V1SignUtil v1SignUtil;

    /** 统一下单 - POST /mapi.php（需MD5签名） */
    public Map<String, Object> createOrder(Map<String, String> params) {
        return postWithSign("/mapi.php", params);
    }

    /** 页面跳转 - GET /submit.php（需MD5签名） */
    public String submitOrder(Map<String, String> params) {
        String url = v1PayProperties.getBaseUrl() + "submit.php";
        Map<String, String> signedParams = addSign(params);
        StringBuilder formUrl = new StringBuilder(url).append("?");
        signedParams.forEach((k, v) -> formUrl.append(k).append("=").append(v).append("&"));
        return formUrl.toString();
    }

    /** 查询订单 - GET /api.php?act=order（用pid+key认证，不签名） */
    public Map<String, Object> queryOrder(Map<String, String> params) {
        StringBuilder url = new StringBuilder(v1PayProperties.getBaseUrl() + "api.php");
        url.append("?act=order");
        url.append("&pid=").append(v1PayProperties.getPid());
        url.append("&key=").append(v1PayProperties.getKey());
        if (params.containsKey("trade_no")) {
            url.append("&trade_no=").append(params.get("trade_no"));
        }
        if (params.containsKey("out_trade_no")) {
            url.append("&out_trade_no=").append(params.get("out_trade_no"));
        }
        log.debug("V1 queryOrder URL: {}", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url.toString(), String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            LogEsUtil.warn("V1查询订单失败，HTTP状态码：" + response.getStatusCode());
            throw new RuntimeException("查询支付平台失败");
        }
        return JSON.parseObject(response.getBody(), new TypeReference<Map<String, Object>>() {});
    }

    /** 订单退款 - POST /api.php?act=refund（用pid+key认证，不签名） */
    public Map<String, Object> refundOrder(Map<String, String> params) {
        String url = v1PayProperties.getBaseUrl() + "api.php?act=refund";
        Map<String, String> allParams = new HashMap<>(params);
        allParams.put("pid", String.valueOf(v1PayProperties.getPid()));
        allParams.put("key", v1PayProperties.getKey());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        allParams.forEach(map::add);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        log.debug("V1 refundOrder URL: {}", url);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            LogEsUtil.warn("V1退款请求失败，HTTP状态码：" + response.getStatusCode());
            throw new RuntimeException("退款请求失败");
        }
        return JSON.parseObject(response.getBody(), new TypeReference<Map<String, Object>>() {});
    }

    /** POST请求（带MD5签名） */
    private Map<String, Object> postWithSign(String uri, Map<String, String> params) {
        String url = v1PayProperties.getBaseUrl() + uri;
        Map<String, String> signedParams = addSign(params);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        signedParams.forEach(map::add);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        log.debug("V1 POST URL: {}", url);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            LogEsUtil.warn("V1请求支付平台失败，HTTP状态码：" + response.getStatusCode());
            throw new RuntimeException("请求支付平台失败");
        }
        return JSON.parseObject(response.getBody(), new TypeReference<Map<String, Object>>() {});
    }

    /** 添加MD5签名参数 */
    private Map<String, String> addSign(Map<String, String> params) {
        Map<String, String> newParams = new HashMap<>(params);
        if (!newParams.containsKey("pid")) {
            newParams.put("pid", String.valueOf(v1PayProperties.getPid()));
        }
        String sign = v1SignUtil.generateSign(newParams, v1PayProperties.getKey());
        newParams.put("sign", sign);
        newParams.put("sign_type", "MD5");
        return newParams;
    }

    /** 验证回调签名 */
    public boolean verifyResponse(Map<String, String> params) {
        String sign = params.remove("sign");
        if (sign == null || sign.isEmpty()) {
            return false;
        }
        params.remove("sign_type");
        return v1SignUtil.verifySign(params, v1PayProperties.getKey(), sign);
    }
}
