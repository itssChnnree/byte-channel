package com.ruoyi.system.config;


import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * TraceId 上下文管理类
 * 使用 ThreadLocal 存储 traceId，支持异步线程传递
 */
public class TraceIdContext {

    /**
     * HTTP 请求头中的 TraceId key
     */
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    /**
     * 使用 TransmittableThreadLocal 替代普通的 ThreadLocal
     * 支持线程池场景下的上下文传递
     */
    private static final ThreadLocal<String> TRACE_ID_HOLDER = new TransmittableThreadLocal<>();
    
    /**
     * 日志中使用的 key（用于 MDC）
     */
    public static final String TRACE_ID_MDC_KEY = "traceId";
    

    
    /**
     * 设置当前线程的 traceId
     */
    public static void setTraceId(String traceId) {
        if (StringUtils.hasText(traceId)) {
            TRACE_ID_HOLDER.set(traceId);
            // 同时设置到 MDC 中，便于日志打印
            MDC.put(TRACE_ID_MDC_KEY, traceId);
        }
    }
    
    /**
     * 获取当前线程的 traceId
     * 如果不存在则生成一个新的
     */
    public static String getTraceId() {
        String traceId = TRACE_ID_HOLDER.get();
        if (traceId == null || traceId.isEmpty()) {
            traceId = generateTraceId();
            setTraceId(traceId);
        }
        return traceId;
    }
    
    /**
     * 获取当前线程的 traceId（如果不存在则返回 null）
     */
    public static String getTraceIdOrNull() {
        return TRACE_ID_HOLDER.get();
    }
    
    /**
     * 清除当前线程的 traceId
     */
    public static void clear() {
        TRACE_ID_HOLDER.remove();
        MDC.remove(TRACE_ID_MDC_KEY);
    }
    
    /**
     * 生成新的 traceId
     */
    public static String generateTraceId() {
        // 使用 UUID 生成，去掉横线
        return UUID.randomUUID().toString().replace("-", "");
    }

    
    /**
     * 判断当前线程是否有 traceId
     */
    public static boolean hasTraceId() {
        return TRACE_ID_HOLDER.get() != null && 
               !TRACE_ID_HOLDER.get().isEmpty();
    }
    

    
    /**
     * 将当前上下文包装为 Runnable，用于线程池提交
     */
    public static Runnable wrap(Runnable runnable) {
        String traceId = getTraceId();
        return () -> {
            setTraceId(traceId);
            try {
                runnable.run();
            } finally {
                clear();
            }
        };
    }
    
    /**
     * 将当前上下文包装为 Callable，用于线程池提交
     */
    public static <T> java.util.concurrent.Callable<T> wrap(java.util.concurrent.Callable<T> callable) {
        String traceId = getTraceId();
        return () -> {
            setTraceId(traceId);
            try {
                return callable.call();
            } finally {
                clear();
            }
        };
    }
    
    /**
     * 初始化当前线程的上下文（通常在请求开始时调用）
     */
    public static String initContext() {
        clear(); // 先清理，确保没有旧的 traceId
        String traceId = generateTraceId();
        setTraceId(traceId);
        return traceId;
    }
    
    /**
     * 初始化当前线程的上下文（使用指定的 traceId）
     */
    public static void initContext(String traceId) {
        clear();
        setTraceId(traceId);
    }
    

    
    /**
     * 恢复上下文快照（在线程切换后调用）
     */
    public static void restoreSnapshot(TraceContextSnapshot snapshot) {
        if (snapshot != null) {
            initContext(snapshot.getTraceId());
        }
    }

    /**
     * 上下文快照类
     */
    public static class TraceContextSnapshot {
        private final String traceId;

        
        public TraceContextSnapshot(String traceId) {
            this.traceId = traceId;
        }
        
        public String getTraceId() {
            return traceId;
        }
        
    }
}