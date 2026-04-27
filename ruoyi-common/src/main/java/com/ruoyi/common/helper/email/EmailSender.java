package com.ruoyi.common.helper.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Locale;

@Component
@Slf4j
public class EmailSender {

    private static final String RESEND_API_URL = "https://api.resend.com/emails";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Resource
    private TemplateEngine templateEngine;

    @Value("${resend.api-key}")
    private String resendApiKey;

    @Value("${resend.from-email}")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail,
                                      String username,
                                      String verificationCode,
                                      String operation,
                                      int expiryMinutes) {
        Context context = new Context(Locale.getDefault());
        context.setVariable("username", username);
        context.setVariable("verificationCode", verificationCode);
        context.setVariable("operation", operation);
        context.setVariable("expiryMinutes", expiryMinutes);

        String emailContent = templateEngine.process("verificationEmail", context);
        send(toEmail, "您的验证码 - " + operation, emailContent);
    }

    public static String generateVerificationCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append((int) (Math.random() * 10));
        }
        return code.toString();
    }

    public void sendResourceExpireNoticeEmail(String toEmail,
                                              String username,
                                              String resourcesIp,
                                              Date expireTime) {
        Context context = new Context(Locale.getDefault());
        context.setVariable("username", username);
        context.setVariable("resourcesIp", resourcesIp);
        context.setVariable("expireTime", expireTime);
        context.setVariable("remainDays", calculateRemainDays(expireTime));

        String emailContent = templateEngine.process("resourceExpireNotice", context);
        send(toEmail, "资源到期提醒 - " + resourcesIp, emailContent);
    }

    public void sendResourceRenewFailEmail(String toEmail,
                                           String username,
                                           String resourcesIp,
                                           String failReason) {
        Context context = new Context(Locale.getDefault());
        context.setVariable("username", username);
        context.setVariable("resourcesIp", resourcesIp);
        context.setVariable("failReason", failReason);
        context.setVariable("currentTime", new Date());

        String emailContent = templateEngine.process("resourceRenewFail", context);
        send(toEmail, "自动续费失败提醒 - " + resourcesIp, emailContent);
    }

    public void sendResourceRenewSuccessEmail(String toEmail,
                                              String username,
                                              String resourcesIp,
                                              String commodityName,
                                              String renewalPrice,
                                              Date newExpireTime) {
        Context context = new Context(Locale.getDefault());
        context.setVariable("username", username);
        context.setVariable("resourcesIp", resourcesIp);
        context.setVariable("commodityName", commodityName);
        context.setVariable("renewalPrice", renewalPrice);
        context.setVariable("newExpireTime", newExpireTime);
        context.setVariable("renewalTime", new Date());

        String emailContent = templateEngine.process("resourceRenewSuccess", context);
        send(toEmail, "自动续费成功通知 - " + resourcesIp, emailContent);
    }

    private void send(String toEmail, String subject, String htmlContent) {
        HttpsURLConnection conn = null;
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("from", fromEmail);
            payload.put("to", toEmail);
            payload.put("subject", subject);
            payload.put("html", htmlContent);

            String json = OBJECT_MAPPER.writeValueAsString(payload);
            byte[] body = json.getBytes(StandardCharsets.UTF_8);

            java.net.URL url = new java.net.URL(RESEND_API_URL);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + resendApiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body);
                os.flush();
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                }
                Map result = OBJECT_MAPPER.readValue(sb.toString(), Map.class);
                log.info("邮件发送成功, id: {}, to: {}, subject: {}", result.get("id"), toEmail, subject);
            } else {
                log.error("邮件发送失败, HTTP {} , to: {}, subject: {}", code, toEmail, subject);
            }
        } catch (Exception e) {
            log.error("邮件发送异常, to: {}, subject: {}", toEmail, subject, e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private long calculateRemainDays(Date expireTime) {
        long diff = expireTime.getTime() - System.currentTimeMillis();
        return Math.max(0, diff / (1000 * 60 * 60 * 24));
    }
}
