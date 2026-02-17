package com.ruoyi.system.util;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.config.TraceIdContext;
import com.ruoyi.system.domain.base.LogEntry;
import com.ruoyi.system.service.IUserOperLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * Elasticsearch 日志工具类
 * 提供 info, error, warn 等日志方法
 */
@Slf4j
@Component
public class LogEsUtil {
    
    private static IUserOperLogService logService;
    
    @Autowired
    private IUserOperLogService autowiredLogService;
    
    /**
     * 应用名称（从配置读取）
     */
    @Value("${spring.application.name:unknown}")
    private String applicationName;
    
    /**
     * 环境标识
     */
    @Value("${spring.profiles.active:dev}")
    private String environment;

    @Resource(name = "traceIdAwareExecutor")
    private Executor traceIdAwareExecutor;

    private static Executor executor;
    
    /**
     * 是否启用 ES 日志
     */
    @Value("${logging.es.enabled:true}")
    private boolean esLogEnabled;

    /**
     * 是否启用 ES 日志
     */
    private static boolean esLogEnabledStatic;
    
    /**
     * 日志等级控制
     */
    @Value("${logging.es.level:INFO}")
    private String logLevel;

    /**
     * 日志等级控制
     */
    private static String logLevelStatic;
    
    /**
     * 初始化静态变量
     */
    @PostConstruct
    public void init() {
        logService = autowiredLogService;
        esLogEnabledStatic = esLogEnabled;
        logLevelStatic = logLevel;
        executor = traceIdAwareExecutor;
        log.info("LogEsUtil 初始化完成，应用名称: {}，环境: {}，ES日志启用: {}",
                applicationName, environment, esLogEnabled);
    }
    
    /**
     * 私有构造函数，防止实例化
     */
    private LogEsUtil() {
    }
    
    /**
     * 记录 INFO 日志
     *
     * @param message 日志消息
     */
    public static void info(String message) {
        info(message, null, null);
    }
    
    /**
     * 记录 INFO 日志
     *
     * @param message     日志消息
     * @param extraFields 额外字段
     */
    public static void info(String message, Map<String, Object> extraFields) {
        info(message, null,  extraFields);
    }
    
    /**
     * 记录 INFO 日志
     *
     * @param message 日志消息
     * @param traceId 追踪ID
     */
    public static void info(String message, String traceId) {
        info(message, traceId, null);
    }
    

    
    /**
     * 记录 INFO 日志（完整参数）
     *
     * @param message     日志消息
     * @param traceId     追踪ID
     * @param extraFields 额外字段
     */
    public static void info(String message, String traceId, Map<String, Object> extraFields) {
        if (!isLogLevelEnabled(LogEntry.LogLevel.INFO)) {
            return;
        }
        String userId1 = SecurityUtils.getLogUserId();
        Runnable runnable = () -> {
            saveLog(LogEntry.LogLevel.INFO, message, null, traceId, userId1, null, null, null,extraFields);
        };
        executor.execute(runnable);
    }

    /**
     * 记录 INFO 日志（完整参数）
     *
     * @param message     日志消息
     * @param traceId     追踪ID
     */
    public static void responseInfo(String message, String traceId, Long responseTime,Integer responseStatus) {
        if (!isLogLevelEnabled(LogEntry.LogLevel.INFO)) {
            return;
        }
        String userId1 = SecurityUtils.getLogUserId();
        Runnable runnable = () -> {
            saveLog(LogEntry.LogLevel.INFO, message, null, traceId, userId1, null, responseTime, responseStatus,null);
        };
        executor.execute(runnable);
    }
    
    /**
     * 记录 ERROR 日志
     *
     * @param message   日志消息
     * @param throwable 异常对象
     */
    public static void error(String message, Throwable throwable) {
        error(message, throwable, null,  null);
    }
    
    /**
     * 记录 ERROR 日志
     *
     * @param message     日志消息
     * @param throwable   异常对象
     * @param extraFields 额外字段
     */
    public static void error(String message, Throwable throwable, Map<String, Object> extraFields) {
        error(message, throwable, null,  extraFields);
    }
    
    /**
     * 记录 ERROR 日志
     *
     * @param message   日志消息
     * @param throwable 异常对象
     * @param traceId   追踪ID
     */
    public static void error(String message, Throwable throwable, String traceId) {
        error(message, throwable, traceId,  null);
    }
    
    /**
     * 记录 ERROR 日志（完整参数）
     *
     * @param message     日志消息
     * @param throwable   异常对象
     * @param traceId     追踪ID
     * @param extraFields 额外字段
     */
    public static void error(String message, Throwable throwable, String traceId, Map<String, Object> extraFields) {
        if (!isLogLevelEnabled(LogEntry.LogLevel.ERROR)) {
            return;
        }
        String userId1 = SecurityUtils.getLogUserId();
        Runnable runnable = () -> {
            saveLog(LogEntry.LogLevel.ERROR, message, throwable, traceId, userId1, null, null,null, extraFields);

        };
        executor.execute(runnable);
    }
    
    /**
     * 记录 WARN 日志
     *
     * @param message 日志消息
     */
    public static void warn(String message) {
        warn(message, null,  null);
    }
    
    /**
     * 记录 WARN 日志
     *
     * @param message     日志消息
     * @param extraFields 额外字段
     */
    public static void warn(String message, Map<String, Object> extraFields) {
        warn(message, null,  extraFields);
    }
    
    /**
     * 记录 WARN 日志
     *
     * @param message 日志消息
     * @param traceId 追踪ID
     */
    public static void warn(String message, String traceId) {
        warn(message, traceId,  null);
    }
    
    /**
     * 记录 WARN 日志（完整参数）
     *
     * @param message     日志消息
     * @param traceId     追踪ID
     * @param extraFields 额外字段
     */
    public static void warn(String message, String traceId, Map<String, Object> extraFields) {
        if (!isLogLevelEnabled(LogEntry.LogLevel.WARN)) {
            return;
        }
        String userId1 = SecurityUtils.getLogUserId();
        Runnable runnable = () -> {
            saveLog(LogEntry.LogLevel.WARN, message, null, traceId, userId1, null, null, null,extraFields);

        };
        executor.execute(runnable);
    }
    

    

    


    
    /**
     * 记录业务操作日志
     *
     * @param operation   操作名称
     * @param module      模块名称
     * @param description 操作描述
     * @param userName    用户名
     * @param success     是否成功
     * @param extraFields 额外字段
     */
    public static void bizLog(String operation, String module, String description, 
                              String userName, boolean success,
                             Map<String, Object> extraFields) {
        
        String message = String.format("业务操作 - %s: %s [%s]", module, operation, description);
        
        Map<String, Object> bizExtraFields = new ConcurrentHashMap<>();
        if (extraFields != null) {
            bizExtraFields.putAll(extraFields);
        }
        bizExtraFields.put("operation", operation);
        bizExtraFields.put("module", module);
        bizExtraFields.put("description", description);
        bizExtraFields.put("userName", userName);
        bizExtraFields.put("success", success);
        
        info(message, null,  bizExtraFields);
    }
    
    /**
     * 记录 API 请求日志
     *
     * @param requestPath    请求路径
     * @param httpMethod     HTTP方法
     * @param responseStatus 响应状态码
     * @param responseTime   响应时间（毫秒）
     * @param extraFields    额外字段
     */
    public static void apiLog(String requestPath, String httpMethod, 
                             Integer responseStatus, Long responseTime,
                             Map<String, Object> extraFields) {
        
        String message = String.format("API请求 - %s %s [状态: %d, 耗时: %dms]", 
                httpMethod, requestPath, responseStatus, responseTime);
        
        Map<String, Object> apiExtraFields = new ConcurrentHashMap<>();
        if (extraFields != null) {
            apiExtraFields.putAll(extraFields);
        }
        apiExtraFields.put("requestPath", requestPath);
        apiExtraFields.put("httpMethod", httpMethod);
        apiExtraFields.put("responseStatus", responseStatus);
        apiExtraFields.put("responseTime", responseTime);
        
        info(message, null, apiExtraFields);
    }
    
    /**
     * 保存日志到 Elasticsearch
     *
     * @param level         日志等级
     * @param message       日志消息
     * @param throwable     异常对象
     * @param traceId       追踪ID
     * @param userId        用户ID
     * @param requestPath   请求路径
     * @param responseTime  响应时间
     * @param extraFields   额外字段
     */
    private static void saveLog(LogEntry.LogLevel level, String message, Throwable throwable, 
                               String traceId, String userId, String requestPath, 
                               Long responseTime, Integer responseStatus,Map<String, Object> extraFields) {
        
        if (!esLogEnabledStatic) {
            log.warn("ES日志未启用，跳过保存: {}", message);
            return;
        }
        
        if (logService == null) {
            log.error("LogService未初始化，无法保存日志到ES: {}", message);
            return;
        }
        
        try {
            // 获取或生成 traceId
            String finalTraceId = StringUtils.hasText(traceId) ? traceId : TraceIdContext.getTraceId();

            String finalUserId = StringUtils.hasText(userId) ? userId : SecurityUtils.getLogUserId();

            // 构建日志实体
            LogEntry logEntry = LogEntry.builder()
                    .id(finalTraceId)
                    .traceId(finalTraceId)
                    .timestamp(LocalDateTime.now())
                    .timestampMs(System.currentTimeMillis())
                    .message(message)
                    .level(level)
                    .threadName(Thread.currentThread().getName())
                    .userId(finalUserId)
                    .responseStatus(responseStatus)
                    .requestPath(requestPath)
                    .responseTime(responseTime)
                    .build();
            
            // 设置异常信息
            if (throwable != null) {
                logEntry.setExceptionClass(throwable.getClass().getName());
                logEntry.setExceptionMessage(throwable.getMessage());
                logEntry.setStackTrace(getStackTrace(throwable));
            }
            
            // 设置额外字段
            if (extraFields != null && !extraFields.isEmpty()) {
                logEntry.setRequestPath((String) extraFields.remove("requestPath"));
                logEntry.setHttpMethod((String) extraFields.remove("httpMethod"));
                String extraFieldsString = JSON.toJSONString(extraFields);
                logEntry.setExtraField(extraFieldsString);
            }
            
            // 保存日志
            logService.save(logEntry);
            
        } catch (Exception e) {
            // 避免因为日志记录失败而影响主流程
            log.error("保存日志到ES失败: {}", message, e);
        }
    }
    
    /**
     * 检查日志等级是否启用
     *
     * @param level 要检查的日志等级
     * @return 是否启用
     */
    private static boolean isLogLevelEnabled(LogEntry.LogLevel level) {
        // 默认的日志等级映射
        java.util.Map<LogEntry.LogLevel, Integer> levelPriority = new java.util.HashMap<>();
        levelPriority.put(LogEntry.LogLevel.INFO, 2);
        levelPriority.put(LogEntry.LogLevel.WARN, 3);
        levelPriority.put(LogEntry.LogLevel.ERROR, 4);
        levelPriority.put(LogEntry.LogLevel.FATAL, 5);
        
        // 配置的日志等级
        LogEntry.LogLevel configLevel;
        try {
            configLevel = LogEntry.LogLevel.valueOf(logLevelStatic.toUpperCase());
        } catch (IllegalArgumentException e) {
            configLevel = LogEntry.LogLevel.INFO; // 默认值
        }
        
        // 只有当要记录的日志等级优先级 >= 配置的日志等级优先级时才记录
        return levelPriority.getOrDefault(level, 0) >= levelPriority.getOrDefault(configLevel, 2);
    }
    
    /**
     * 获取调用者类名
     *
     * @return 调用者类名
     */
    private static String getCallerClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // stackTrace[0] 是 getStackTrace 方法
        // stackTrace[1] 是 getCallerClassName 方法
        // stackTrace[2] 是 saveLog 方法
        // stackTrace[3] 是 info/error/warn 等方法
        // stackTrace[4] 是实际的调用者
        if (stackTrace.length > 4) {
            return stackTrace[4].getClassName();
        }
        return "Unknown";
    }
    
    /**
     * 获取异常堆栈信息
     *
     * @param throwable 异常对象
     * @return 堆栈信息字符串
     */
    private static String getStackTrace(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
    
    /**
     * 获取日志服务实例（用于特殊情况）
     */
    public static IUserOperLogService getLogService() {
        return logService;
    }
    
    /**
     * 检查 ES 日志是否启用
     */
    public static boolean isEsLogEnabled() {
        return esLogEnabledStatic;
    }
}