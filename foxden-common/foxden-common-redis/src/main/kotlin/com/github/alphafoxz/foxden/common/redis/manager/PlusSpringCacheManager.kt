package com.github.alphafoxz.foxden.common.redis.manager

import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import org.redisson.api.RMap
import org.redisson.api.RMapCache
import org.redisson.spring.cache.CacheConfig
import org.redisson.spring.cache.RedissonCache
import org.springframework.boot.convert.DurationStyle
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.transaction.TransactionAwareCacheDecorator
import org.springframework.util.StringUtils
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * 基于 Redisson 的 CacheManager 实现
 *
 * 支持多参数格式: cacheNames#ttl#maxIdleTime#maxSize#local
 * - cacheNames: 缓存名称
 * - ttl: 过期时间（如 60s, 10m, 1h, 1d）
 * - maxIdleTime: 最大空闲时间（如 10m, 1h）
 * - maxSize: 缓存最大条数
 * - local: 是否启用本地缓存（1=启用，0=禁用）
 *
 * 示例:
 * - "demo:cache#60s#10m#20" -> TTL=60秒，空闲时间=10分钟，最大20条，启用本地缓存
 * - "test#1h" -> TTL=1小时，其他默认
 * - "simple#0#0#0#0" -> 不过期，无空闲限制，无大小限制，禁用本地缓存
 *
 * @author Lion Li (based on ruoyi)
 */
class PlusSpringCacheManager : CacheManager {

    private var dynamic = true
    private var allowNullValues = true
    private var transactionAware = true

    val configMap: MutableMap<String, CacheConfig> = ConcurrentHashMap()
    private val instanceMap: ConcurrentMap<String, Cache> = ConcurrentHashMap()

    /**
     * 设置是否允许存储 null 值
     */
    fun setAllowNullValues(allowNullValues: Boolean) {
        this.allowNullValues = allowNullValues
    }

    /**
     * 设置是否支持事务感知
     */
    fun setTransactionAware(transactionAware: Boolean) {
        this.transactionAware = transactionAware
    }

    /**
     * 定义固定的缓存名称
     * 设置后将不允许动态创建缓存
     */
    fun setCacheNames(names: Collection<String>?) {
        if (names != null) {
            for (name in names) {
                getCache(name)
            }
            dynamic = false
        } else {
            dynamic = true
        }
    }

    /**
     * 设置缓存配置
     */
    fun setConfig(config: Map<String, CacheConfig>) {
        configMap.clear()
        configMap.putAll(config)
    }

    protected fun createDefaultConfig(): CacheConfig {
        return CacheConfig()
    }

    override fun getCache(name: String): Cache? {
        // 解析多参数格式: cacheNames#ttl#maxIdleTime#maxSize#local
        val array = StringUtils.delimitedListToStringArray(name, "#")
        val cacheName = array[0]

        // 检查是否已存在缓存实例
        val existingCache = instanceMap[cacheName]
        if (existingCache != null) {
            return existingCache
        }

        // 非动态模式，未配置的缓存返回 null
        if (!dynamic) {
            return null
        }

        // 获取或创建配置
        var config = configMap[cacheName]
        if (config == null) {
            config = createDefaultConfig()
            configMap[cacheName] = config
        }

        // 解析参数
        val local = if (array.size > 4) array[4].toInt() else 1

        if (array.size > 1) {
            config.setTTL(DurationStyle.detectAndParse(array[1]).toMillis())
        }
        if (array.size > 2) {
            config.setMaxIdleTime(DurationStyle.detectAndParse(array[2]).toMillis())
        }
        if (array.size > 3) {
            config.setMaxSize(array[3].toInt())
        }

        // 根据配置创建缓存
        return if (config.maxIdleTime == 0L && config.ttl == 0L && config.maxSize == 0) {
            createMap(cacheName, config, local)
        } else {
            createMapCache(cacheName, config, local)
        }
    }

    /**
     * 创建普通 RMap 缓存
     */
    private fun createMap(name: String, config: CacheConfig, local: Int): Cache {
        val map: RMap<Any, Any> = RedisUtils.getClient().getMap(name)

        var cache: Cache = RedissonCache(map, allowNullValues)

        if (local == 1) {
            cache = CaffeineCacheDecorator(name, cache)
        }
        if (transactionAware) {
            cache = TransactionAwareCacheDecorator(cache)
        }

        val oldCache = instanceMap.putIfAbsent(name, cache)
        return oldCache ?: cache
    }

    /**
     * 创建带 TTL 和 MaxIdleTime 的 RMapCache
     */
    private fun createMapCache(name: String, config: CacheConfig, local: Int): Cache {
        val map: RMapCache<Any, Any> = RedisUtils.getClient().getMapCache(name)

        var cache: Cache = RedissonCache(map, config, allowNullValues)

        if (local == 1) {
            cache = CaffeineCacheDecorator(name, cache)
        }
        if (transactionAware) {
            cache = TransactionAwareCacheDecorator(cache)
        }

        val oldCache = instanceMap.putIfAbsent(name, cache)
        if (oldCache == null) {
            map.setMaxSize(config.maxSize)
        }
        return oldCache ?: cache
    }

    override fun getCacheNames(): Collection<String> {
        return Collections.unmodifiableSet(configMap.keys)
    }
}
