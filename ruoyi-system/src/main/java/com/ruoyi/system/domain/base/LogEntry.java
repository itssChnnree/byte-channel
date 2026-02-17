package com.ruoyi.system.domain.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 日志实体类
 * 用于存储到 Elasticsearch
 * @author a1152
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogEntry {
    
    /**
     * Elasticsearch 文档 ID
     */
    @Id
    private String id;
    
    /**
     * 追踪 ID (用于分布式追踪)
     */
    @Field(type = FieldType.Keyword)
    private String traceId;

    /**
     * 日志时间戳
     */
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_fraction)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime timestamp;
    
    /**
     * 日志产生时间（Unix 时间戳，毫秒）
     */
    @Field(type = FieldType.Long)
    private Long timestampMs;
    
    /**
     * 日志消息
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String message;
    
    /**
     * 日志等级
     * ERROR > WARN > INFO > DEBUG > TRACE
     */
    @Field(type = FieldType.Keyword)
    private LogLevel level;

    
    /**
     * 线程名称
     */
    @Field(type = FieldType.Keyword)
    private String threadName;

    
    /**
     * 请求路径 (如果是 Web 请求)
     */
    @Field(type = FieldType.Keyword)
    private String requestPath;
    
    /**
     * HTTP 方法
     */
    @Field(type = FieldType.Keyword)
    private String httpMethod;
    
    /**
     * 用户 ID
     */
    @Field(type = FieldType.Keyword)
    private String userId;
    
    /**
     * 用户名称
     */
    @Field(type = FieldType.Keyword)
    private String userName;
    
    /**
     * 异常堆栈信息
     */
    @Field(type = FieldType.Text)
    private String stackTrace;
    
    /**
     * 异常类名
     */
    @Field(type = FieldType.Keyword)
    private String exceptionClass;
    
    /**
     * 异常消息
     */
    @Field(type = FieldType.Text)
    private String exceptionMessage;
    
    /**
     * 响应状态码
     */
    @Field(type = FieldType.Integer)
    private Integer responseStatus;
    
    /**
     * 响应时间 (毫秒)
     */
    @Field(type = FieldType.Long)
    private Long responseTime;
    
    /**
     * 自定义扩展字段
     */
    @Field(type = FieldType.Object)
    private Map<String, Object> extraFields;

    /**
     * 自定义扩展字段
     */
    @Field(type = FieldType.Text)
    private String extraField;
    
    /**
     * 日志等级枚举
     */
    public enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR, FATAL
    }
    
    /**
     * 预构建方法，简化创建日志对象
     */
    public static LogEntry create(String message, LogLevel level, String traceId) {
        return LogEntry.builder()
                .traceId(traceId)
                .timestamp(LocalDateTime.now())
                .timestampMs(System.currentTimeMillis())
                .message(message)
                .level(level)
                .timestamp(LocalDateTime.now())
                .threadName(Thread.currentThread().getName())
                .build();
    }
    
    /**
     * 创建错误日志
     */
    public static LogEntry error(String message, Throwable throwable, String traceId) {
        return LogEntry.builder()
                .traceId(traceId)
                .timestamp(LocalDateTime.now())
                .timestampMs(System.currentTimeMillis())
                .message(message)
                .level(LogLevel.ERROR)
                .exceptionClass(throwable.getClass().getName())
                .exceptionMessage(throwable.getMessage())
                .stackTrace(getStackTrace(throwable))
                .threadName(Thread.currentThread().getName())
                .build();
    }
    
    /**
     * 生成简单的 spanId
     */
    private static String generateSpanId() {
        return Long.toHexString(System.currentTimeMillis()) + 
               "-" + 
               Integer.toHexString((int)(Math.random() * 10000));
    }
    
    /**
     * 获取异常堆栈信息
     */
    private static String getStackTrace(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}