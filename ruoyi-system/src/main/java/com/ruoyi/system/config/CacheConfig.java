package com.ruoyi.system.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/25
 */
@Configuration
@Slf4j
public class CacheConfig {

    //订单锁缓存
    @Bean(name = "orderCache")
    public Cache<String, ReentrantLock> caffeineConfig() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(3000)
                //距离上次推送一个小时以后过期
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
    }

    //traceId 请求开始时间记录
    @Bean(name = "requestStartTime")
    public Cache<String, Long> requestStartTime() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(3000)
                //距离上次推送一个小时以后过期
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    //traceId 缓存至相同索引
    @Bean(name = "traceIdIndex")
    public Cache<String, String> traceIdIndex() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(3000)
                //距离上次推送一个小时以后过期
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }
}
