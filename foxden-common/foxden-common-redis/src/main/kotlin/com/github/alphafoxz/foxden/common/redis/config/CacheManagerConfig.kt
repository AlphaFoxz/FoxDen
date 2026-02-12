package com.github.alphafoxz.foxden.common.redis.config

import com.github.alphafoxz.foxden.common.redis.manager.PlusSpringCacheManager
import com.github.benmanes.caffeine.cache.Cache as CaffeineCache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import java.util.concurrent.TimeUnit

/**
 * 缓存配置类
 *
 * 使用 PlusSpringCacheManager 整合 Spring Cache
 * 支持:
 * - Redis 分布式缓存
 * - Caffeine 本地二级缓存
 * - 多参数缓存配置 (cacheNames#ttl#maxIdleTime#maxSize#local)
 */
@AutoConfiguration
@EnableCaching
class CacheManagerConfig {

    /**
     * Caffeine 本地缓存处理器
     * 用于二级缓存装饰器
     *
     * 配置:
     * - 过期时间: 30秒（写入或访问后）
     * - 初始容量: 100
     * - 最大容量: 1000
     */
    @Bean("caffeineCache")
    @ConditionalOnMissingBean(name = ["caffeineCache"])
    fun caffeineCache(): CaffeineCache<Any, Any> {
        return Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .initialCapacity(100)
            .maximumSize(1000)
            .recordStats()
            .build()
    }

    /**
     * 自定义缓存管理器
     * 整合 Spring Cache 和 Redisson
     */
    @Bean
    @ConditionalOnMissingBean
    fun cacheManager(): CacheManager {
        val manager = PlusSpringCacheManager()
        manager.setAllowNullValues(true)
        manager.setTransactionAware(true)
        return manager
    }
}
