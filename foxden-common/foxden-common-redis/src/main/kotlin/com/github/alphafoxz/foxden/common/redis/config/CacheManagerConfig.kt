package com.github.alphafoxz.foxden.common.redis.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import java.util.concurrent.TimeUnit

/**
 * 缓存配置类
 * 主要使用 RedisUtils，Caffeine 作为本地缓存补充
 */
@AutoConfiguration
@EnableCaching
class CacheManagerConfig {

    /**
     * Caffeine 本地缓存管理器
     * 用于 Spring Cache 注解支持
     */
    @Bean
    @ConditionalOnMissingBean
    fun cacheManager(): CacheManager {
        val caffeine = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .recordStats()

        return CaffeineCacheManager().apply {
            setCaffeine(caffeine)
            setAllowNullValues(true)
        }
    }
}
