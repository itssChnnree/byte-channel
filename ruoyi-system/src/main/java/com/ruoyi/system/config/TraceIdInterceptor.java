package com.ruoyi.system.config;


import com.ruoyi.system.util.LogEsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * TraceId 拦截器
 * 自动从请求头获取或生成 traceId
 */
@Slf4j
@Component
public class TraceIdInterceptor implements HandlerInterceptor {

    @Resource(name = "requestStartTime")
    private Cache<String,Long> cache;


    // 忽略的请求路径
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/actuator/**", "/health", "/swagger-ui/**", "/v3/api-docs/**",
            "/webjars/**", "/swagger-resources/**", "/favicon.ico", "/error",
            "/login", "/register", "/captchaImage","/getEmailCode","/order/download",
            "/resourceAllocationTemporaryStorage/add","/serverResources/download",
            "/serverResources/getBase64VlessUrl","/ticket/getNeedUserReply","/ticket/getNeedServiceReply",
            "/operLog/getDateHistogram"
    );

    // 需要记录请求体的路径模式
    private static final List<String> LOG_REQUEST_BODY_PATHS = Arrays.asList(
            "/commodityCategory/**", "/commodity/**", "/failedDomainBlockingLog/**",
            "/faultHandling/**", "/orderCommodity/**", "/orderCommodityResources/**",
            "/order/**", "/orderRenewalResources/**", "/promoCodeRecords/**",
            "/promoRecords/**", "/resourceAllocationTemporaryStorage/**", "/resourceBlockDomain/**",
            "/scheduledDomainLockingTime/**", "/serverResources/**", "/serverResourcesRenewal/**",
            "/serverResourcesXrayValid/**", "/shopNotice/**", "/shopNoticeFile/**",
            "/shoppingCart/**", "/ticket/**", "/ticketMainText/**",
            "/ticketMainTextFile/**", "/vendorAccountInformation/**", "/vendorInformation/**",
            "/walletBalance/**", "/walletBalanceDetail/**"
            );

    // 敏感参数列表，不记录到日志
    private static final Set<String> SENSITIVE_PARAMS = new HashSet<>(Arrays.asList(
            "password", "pwd", "pass", "token", "secret", "key", "credential",
            "access_token", "refresh_token", "authorization", "card_number",
            "credit_card", "cvv", "security_code", "ssn", "id_card"
    ));

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {

        // 检查是否是排除的路径
        String requestPath = request.getRequestURI();
        if (isExcludePath(requestPath)) {
            return true;
        }

        // 尝试从请求头获取 traceId
        String traceId = request.getHeader(TraceIdContext.TRACE_ID_HEADER);

        // 如果请求头中没有，则生成新的
        if (traceId == null || traceId.isEmpty()) {
            traceId = TraceIdContext.generateTraceId();

        }

        // 设置到当前线程上下文
        TraceIdContext.initContext(traceId);

        // 将 traceId 设置到响应头，方便后续调用链使用
        response.setHeader(TraceIdContext.TRACE_ID_HEADER, traceId);

        // 将请求开始时间存入请求属性
        cache.put(traceId, System.currentTimeMillis());

        // 记录请求开始日志
        logRequestStart(request);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {

        // 检查是否是排除的路径
        String requestPath = request.getRequestURI();
        if (isExcludePath(requestPath)) {
            return;
        }

        try {
            // 获取请求开始时间
            Long startTime = cache.getIfPresent(response.getHeader(TraceIdContext.TRACE_ID_HEADER));
            long responseTime = startTime != null ? System.currentTimeMillis() - startTime : 0;

            // 获取响应状态码
            int statusCode = response.getStatus();

            logResponseEnd(responseTime, String.valueOf(statusCode));

        } finally {
            // 请求完成后清理 ThreadLocal，防止内存泄漏
            TraceIdContext.clear();
        }
    }

    /**
     * 记录请求开始日志
     */
    private void logRequestStart(HttpServletRequest request) {
        try {
            String message = String.format("接口请求开始 - %s %s",
                    request.getMethod(), request.getRequestURI());

            // 获取请求参数（过滤敏感信息）
            String requestParams = getRequestParams(request);
            Map<String, Object> extraFields = new HashMap<>();
            extraFields.put("requestPath", request.getRequestURI());
            extraFields.put("httpMethod", request.getMethod());
            extraFields.put("clientIp", getClientIp(request));
            extraFields.put("requestParams", requestParams);
            LogEsUtil.info(message, extraFields);

        } catch (Exception e) {
            log.error("记录请求开始日志失败", e);
        }
    }


    /**
     * 记录请求开始日志
     */
    private void logResponseEnd(Long time,String statusCode) {
        try {
            String message = String.format("请求结束 - 耗时：%s,结果码：%s", time,statusCode);
            LogEsUtil.responseInfo(message, TraceIdContext.getTraceId(), time,Integer.parseInt(statusCode));
        } catch (Exception e) {
            log.error("记录请求开始日志失败", e);
        }
    }


    /**
     * 构建额外字段
     */
    private Map<String, Object> buildExtraFields(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 Exception ex) {
        Map<String, Object> extraFields = new HashMap<>();

        try {
            // 异常信息
            if (ex != null) {
                extraFields.put("exception", ex.getClass().getName());
                extraFields.put("exceptionMessage", ex.getMessage());
            }

            // 响应信息
            Map<String, String> responseHeaders = new HashMap<>();
            Collection<String> responseHeaderNames = response.getHeaderNames();
            for (String headerName : responseHeaderNames) {
                if (!isSensitiveHeader(headerName)) {
                    responseHeaders.put(headerName, response.getHeader(headerName));
                }
            }
            extraFields.put("responseHeaders", responseHeaders);

        } catch (Exception e) {
            log.error("构建额外字段失败", e);
        }

        return extraFields;
    }

    /**
     * 获取请求参数（过滤敏感信息）
     */
    private String getRequestParams(HttpServletRequest request) {
        StringBuilder params = new StringBuilder();

        try {
            // 获取查询参数
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (parameterMap != null && !parameterMap.isEmpty()) {
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    String paramName = entry.getKey();
                    String[] paramValues = entry.getValue();

                    // 过滤敏感参数
                    if (isSensitiveParam(paramName)) {
                        params.append(paramName).append("=***&");
                        continue;
                    }

                    if (paramValues != null && paramValues.length > 0) {
                        String paramValue = paramValues.length == 1 ?
                                paramValues[0] : Arrays.toString(paramValues);

                        // 截断过长的参数值
                        if (paramValue.length() > 200) {
                            paramValue = paramValue.substring(0, 200) + "...[truncated]";
                        }

                        params.append(paramName).append("=").append(paramValue).append("&");
                    }
                }

                // 移除最后一个 &
                if (params.length() > 0 && params.charAt(params.length() - 1) == '&') {
                    params.deleteCharAt(params.length() - 1);
                }
            }

            // 对于特定路径，尝试获取请求体
            if (shouldLogRequestBody(request.getRequestURI())) {
                String requestBody = getRequestBody(request);
                if (requestBody != null && !requestBody.isEmpty()) {
                    if (params.length() > 0) {
                        params.append(" | ");
                    }
                    params.append("body: ").append(requestBody);
                }
            }

        } catch (Exception e) {
            log.error("获取请求参数失败", e);
        }

        return params.length() > 0 ? params.toString() : "";
    }

    /**
     * 获取请求体
     */
    private String getRequestBody(HttpServletRequest request) {
        try {
            // 如果请求已经被包装
            if (request instanceof ContentCachingRequestWrapper) {
                ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
                byte[] content = wrapper.getContentAsByteArray();
                if (content.length > 0) {
                    String body = new String(content, StandardCharsets.UTF_8);

                    // 过滤敏感信息（如果是 JSON 格式）
                    body = filterSensitiveJsonFields(body);

                    // 截断过长的请求体
                    if (body.length() > 500) {
                        body = body.substring(0, 500) + "...[truncated]";
                    }

                    return body;
                }
            }

            // 对于其他情况，尝试从输入流读取
            String contentType = request.getContentType();
            if (contentType != null && contentType.contains("application/json")) {
                // 注意：这里读取流后，后续处理就无法再读取了
                // 在生产环境中应该使用 ContentCachingRequestWrapper
                String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

                // 过滤敏感信息
                body = filterSensitiveJsonFields(body);

                // 截断
                if (body.length() > 500) {
                    body = body.substring(0, 500) + "...[truncated]";
                }

                return body;
            }

        } catch (IOException e) {
            log.warn("读取请求体失败", e);
        }

        return null;
    }

    /**
     * 过滤 JSON 中的敏感字段
     */
    private String filterSensitiveJsonFields(String json) {
        if (json == null || json.isEmpty()) {
            return json;
        }

        try {
            // 简单的敏感字段过滤
            for (String sensitiveField : SENSITIVE_PARAMS) {
                // 替换 "field": "value" 为 "field": "***"
                String pattern = "\"" + sensitiveField + "\"\\s*:\\s*\"[^\"]*\"";
                json = json.replaceAll(pattern, "\"" + sensitiveField + "\":\"***\"");

                // 处理数字类型的敏感字段
                pattern = "\"" + sensitiveField + "\"\\s*:\\s*\\d+";
                json = json.replaceAll(pattern, "\"" + sensitiveField + "\":\"***\"");

                // 处理布尔类型的敏感字段
                pattern = "\"" + sensitiveField + "\"\\s*:\\s*(true|false)";
                json = json.replaceAll(pattern, "\"" + sensitiveField + "\":\"***\"");
            }
        } catch (Exception e) {
            log.warn("过滤敏感字段失败", e);
        }

        return json;
    }

    /**
     * 获取客户端 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 对于多个代理的情况，取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 检查是否是敏感参数
     */
    private boolean isSensitiveParam(String paramName) {
        if (paramName == null) {
            return false;
        }
        String lowerParamName = paramName.toLowerCase();
        return SENSITIVE_PARAMS.stream()
                .anyMatch(sensitive -> lowerParamName.contains(sensitive.toLowerCase()));
    }

    /**
     * 检查是否是敏感请求头
     */
    private boolean isSensitiveHeader(String headerName) {
        if (headerName == null) {
            return false;
        }
        String lowerHeaderName = headerName.toLowerCase();
        return lowerHeaderName.contains("authorization") ||
                lowerHeaderName.contains("cookie") ||
                lowerHeaderName.contains("password") ||
                lowerHeaderName.contains("token") ||
                lowerHeaderName.contains("secret") ||
                lowerHeaderName.contains("key");
    }

    /**
     * 检查是否应该记录请求体
     */
    private boolean shouldLogRequestBody(String requestPath) {
        if (requestPath == null || requestPath.isEmpty()) {
            return false;
        }
        return LOG_REQUEST_BODY_PATHS.stream()
                .anyMatch(pattern -> requestPath.startsWith(pattern.replace("**", "")));
    }

    /**
     * 检查是否是排除的路径
     */
    private boolean isExcludePath(String requestPath) {
        if (requestPath == null || requestPath.isEmpty()) {
            return true;
        }
        return EXCLUDE_PATHS.stream()
                .anyMatch(pattern -> matchesPattern(requestPath, pattern));
    }

    /**
     * 简单的路径模式匹配
     */
    private boolean matchesPattern(String path, String pattern) {
        if (pattern.contains("**")) {
            String prefix = pattern.replace("**", "");
            return path.startsWith(prefix);
        }
        return path.equals(pattern);
    }
}