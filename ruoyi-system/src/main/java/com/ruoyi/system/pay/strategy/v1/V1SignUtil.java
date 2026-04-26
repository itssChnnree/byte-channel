package com.ruoyi.system.pay.strategy.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/**
 * [V1 MD5签名工具]
 * 小白云OS签名算法：md5(a=b&c=d&e=f + KEY)
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/4/6
 */
@Slf4j
@Component("v1SignUtil")
@ConditionalOnProperty(name = "pay.platform.version", havingValue = "v1")
public class V1SignUtil {

    public String generateSign(Map<String, String> params, String key) {
        String signContent = getSignContent(params);
        String signStr = signContent + key;
        log.debug("V1 sign content: {}", signContent);
        return md5(signStr);
    }

    public boolean verifySign(Map<String, String> params, String key, String sign) {
        if (sign == null || sign.isEmpty()) {
            return false;
        }
        String expectedSign = generateSign(params, key);
        return expectedSign.equalsIgnoreCase(sign);
    }

    private String getSignContent(Map<String, String> params) {
        Map<String, String> filteredParams = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (v != null && !v.isEmpty()
                    && !"sign".equals(k)
                    && !"sign_type".equals(k)) {
                filteredParams.put(k, v);
            }
        }
        List<String> keys = new ArrayList<>(filteredParams.keySet());
        Collections.sort(keys);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String k = keys.get(i);
            content.append(k).append("=").append(filteredParams.get(k));
            if (i < keys.size() - 1) {
                content.append("&");
            }
        }
        return content.toString();
    }

    private String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
}
