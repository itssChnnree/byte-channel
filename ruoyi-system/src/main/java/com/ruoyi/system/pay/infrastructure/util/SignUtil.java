package com.ruoyi.system.pay.infrastructure.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;

/**
 * [RSA签名工具类]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Slf4j
@Component
public class SignUtil {

    private static final String SIGN_TYPE = "RSA";
    private static final String SIGN_ALGORITHM = "SHA256WithRSA";
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * 生成RSA签名（SHA256WithRSA）
     *
     * @param params     参数Map（不含sign、sign_type和空值）
     * @param privateKey 商户私钥（PEM格式）
     * @return Base64编码的签名字符串
     */
    public String generateSign(Map<String, String> params, String privateKey) {
        try {
            String signContent = getSignContent(params);
            log.debug("Sign content: {}", signContent);

            PrivateKey key = loadPrivateKey(privateKey);
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initSign(key);
            signature.update(signContent.getBytes(StandardCharsets.UTF_8));

            return Base64.encodeBase64String(signature.sign());
        } catch (Exception e) {
            log.error("Generate sign failed", e);
            throw new RuntimeException("生成签名失败", e);
        }
    }

    /**
     * 验证RSA签名（SHA256WithRSA）
     *
     * @param params    参数Map
     * @param publicKey 平台公钥（PEM格式）
     * @param sign      Base64编码的签名
     * @return 是否验证通过
     */
    public boolean verifySign(Map<String, String> params, String publicKey, String sign) {
        try {
            String signContent = getSignContent(params);
            log.debug("Verify sign content: {}", signContent);

            PublicKey key = loadPublicKey(publicKey);
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initVerify(key);
            signature.update(signContent.getBytes(StandardCharsets.UTF_8));

            return signature.verify(Base64.decodeBase64(sign));
        } catch (Exception e) {
            log.error("Verify sign failed", e);
            return false;
        }
    }


    /**
     * 获取待签名内容
     */
    private String getSignContent(Map<String, String> params) {
        // 过滤空值、sign、sign_type
        Map<String, String> filteredParams = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null && !value.isEmpty()
                    && !"sign".equals(key)
                    && !"sign_type".equals(key)) {
                filteredParams.put(key, value);
            }
        }

        // 按ASCII码排序
        List<String> keys = new ArrayList<>(filteredParams.keySet());
        Collections.sort(keys);

        // 拼接成 key=value&key2=value2 格式
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = filteredParams.get(key);
            content.append(key).append("=").append(value);
            if (i < keys.size() - 1) {
                content.append("&");
            }
        }

        return content.toString();
    }

    /**
     * 加载私钥
     */
    private PrivateKey loadPrivateKey(String privateKey) throws Exception {
        String key = privateKey
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.decodeBase64(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePrivate(spec);
    }

    /**
     * 加载公钥
     */
    private PublicKey loadPublicKey(String publicKey) throws Exception {
        String key = publicKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.decodeBase64(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePublic(spec);
    }

    /**
     * 获取当前时间戳（10位秒级）
     *
     * @return 时间戳字符串
     */
    public String getTimestamp() {
        return String.valueOf(Instant.now().getEpochSecond());
    }
}
