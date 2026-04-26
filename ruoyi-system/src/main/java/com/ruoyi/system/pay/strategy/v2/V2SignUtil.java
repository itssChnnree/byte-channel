package com.ruoyi.system.pay.strategy.v2;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;

/**
 * [V2 RSA签名工具]
 * 佳梦云付签名算法：SHA256WithRSA
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/4/6
 */
@Slf4j
@Component("v2SignUtil")
@ConditionalOnProperty(name = "pay.platform.version", havingValue = "v2", matchIfMissing = true)
public class V2SignUtil {

    private static final String SIGN_ALGORITHM = "SHA256WithRSA";
    private static final String KEY_ALGORITHM = "RSA";

    public String generateSign(Map<String, String> params, String privateKey) {
        try {
            String signContent = getSignContent(params);
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

    public boolean verifySign(Map<String, String> params, String publicKey, String sign) {
        try {
            String signContent = getSignContent(params);
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

    private String getSignContent(Map<String, String> params) {
        Map<String, String> filteredParams = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null && !value.isEmpty() && !"sign".equals(key) && !"sign_type".equals(key)) {
                filteredParams.put(key, value);
            }
        }
        List<String> keys = new ArrayList<>(filteredParams.keySet());
        Collections.sort(keys);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            content.append(keys.get(i)).append("=").append(filteredParams.get(keys.get(i)));
            if (i < keys.size() - 1) {
                content.append("&");
            }
        }
        return content.toString();
    }

    private PrivateKey loadPrivateKey(String privateKey) throws Exception {
        String key = privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");
        byte[] keyBytes = Base64.decodeBase64(key);
        return KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    private PublicKey loadPublicKey(String publicKey) throws Exception {
        String key = publicKey.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "").replaceAll("\\s", "");
        byte[] keyBytes = Base64.decodeBase64(key);
        return KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    public String getTimestamp() {
        return String.valueOf(Instant.now().getEpochSecond());
    }
}
