package com.ruoyi.common.helper.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Locale;


/**
 * [邮箱发送类]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/7/26
 */
@Component
@Slf4j
public class EmailSender {

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Resource
    private TemplateEngine templateEngine;


    /**
     * 发送验证码邮件
     *
     * @param toEmail 收件人邮箱
     * @param username 用户名
     * @param verificationCode 验证码
     * @param operation 操作名称（如：注册、登录、修改密码等）
     * @param expiryMinutes 验证码有效期（分钟）
     */
    public void sendVerificationEmail(String toEmail,
                                      String username,
                                      String verificationCode,
                                      String operation,
                                      int expiryMinutes) throws MessagingException {

        // 创建 Thymeleaf 上下文并设置变量
        Context context = new Context(Locale.getDefault());
        context.setVariable("username", username);
        context.setVariable("verificationCode", verificationCode);
        context.setVariable("operation", operation);
        context.setVariable("expiryMinutes", expiryMinutes);

        // 使用模板引擎处理模板
        String emailContent = templateEngine.process("verificationEmail", context);

        // 创建 MIME 邮件
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // 设置邮件基本信息
        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject("您的验证码 - " + operation);

        // 设置邮件内容为 HTML
        helper.setText(emailContent, true);

        // 发送邮件
        mailSender.send(message);
    }

    /**
     * 生成随机验证码
     *
     * @param length 验证码长度
     * @return 随机数字验证码
     */
    public static String generateVerificationCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append((int) (Math.random() * 10));
        }
        return code.toString();
    }


}

