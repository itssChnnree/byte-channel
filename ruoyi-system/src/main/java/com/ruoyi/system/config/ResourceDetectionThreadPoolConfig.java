package com.ruoyi.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 资源检测线程池配置
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-18
 */
@Configuration
public class ResourceDetectionThreadPoolConfig {

    @Bean("resourceDetectionExecutor")
    public Executor resourceDetectionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("resource-detect-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean("resourceBlockDomainExecutor")
    public Executor resourceBlockDomainExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("resource-blockDomain-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean("errorRefundExecutor")
    public ThreadPoolTaskExecutor errorRefundExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：根据预期的并发订单数设置
        executor.setCorePoolSize(10);
        // 最大线程数：处理峰值并发
        executor.setMaxPoolSize(30);
        // 队列容量：缓冲待处理的订单
        executor.setQueueCapacity(200);
        // 线程名前缀，便于日志排查
        executor.setThreadNamePrefix("error-refund-");
        // 拒绝策略：由调用线程（主线程）执行，保证任务不丢失
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 关闭时等待的最大时间（秒）
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
