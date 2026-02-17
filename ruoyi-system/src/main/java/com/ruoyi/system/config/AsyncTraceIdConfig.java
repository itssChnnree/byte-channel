package com.ruoyi.system.config;

import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.threadpool.TtlExecutors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步任务配置，支持 TraceId 传递
 */
@Configuration
@EnableAsync
public class AsyncTraceIdConfig implements AsyncConfigurer {
    
    @Bean("traceIdAwareExecutor")
    public Executor traceIdAwareExecutor() {
        // 创建基础线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                // 核心线程数
            5,
                // 最大线程数
            10,
                // 空闲线程存活时间
            60,
            TimeUnit.SECONDS,
                // 任务队列
            new LinkedBlockingQueue<>(1000),
                // 拒绝策略
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
        
        // 使用 TtlExecutors 包装，支持 TransmittableThreadLocal
        return TtlExecutors.getTtlExecutor(executor);
    }
    
    @Override
    public Executor getAsyncExecutor() {
        // 设置 Spring 异步任务使用的执行器
        return traceIdAwareExecutor();
    }
}