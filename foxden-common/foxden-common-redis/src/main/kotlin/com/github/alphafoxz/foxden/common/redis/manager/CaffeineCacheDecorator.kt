package com.github.alphafoxz.foxden.common.redis.manager

import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.benmanes.caffeine.cache.Cache as CaffeineCache
import org.springframework.cache.Cache
import java.util.concurrent.Callable

/**
 * 二级缓存装饰器
 *
 * 本地 Caffeine 缓存 + Redis 缓存
 *
 * 策略:
 * - get: 先查本地 Caffeine，未命中再查 Redis，并回填本地
 * - put: 先清除本地缓存，再存 Redis（避免脏数据）
 * - evict: 同时清除本地和 Redis
 *
 * @param cacheName 缓存名称
 * @param cache 底层 Redis 缓存实现
 *
 * @author Lion Li (based on ruoyi)
 */
class CaffeineCacheDecorator(
    private val cacheName: String,
    private val cache: Cache
) : Cache {

    companion object {
        /**
         * 获取 Caffeine 本地缓存 Bean
         */
        private val CAFFEINE: CaffeineCache<Any, Any> by lazy {
            SpringUtils.getBean("caffeineCache")
        }
    }

    /**
     * 生成唯一键: cacheName:key
     */
    private fun getUniqueKey(key: Any): String {
        return "$cacheName:$key"
    }

    override fun getName(): String {
        return cacheName
    }

    override fun getNativeCache(): Any {
        return cache.nativeCache
    }

    override fun get(key: Any): Cache.ValueWrapper? {
        // 先查本地 Caffeine，使用 get() 方法，未命中会自动加载 Redis 数据
        val localValue: Any? = CAFFEINE.get(getUniqueKey(key)) { k ->
            cache.get(key)
        }
        return localValue as? Cache.ValueWrapper
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(key: Any, type: Class<T>?): T? {
        // 先查本地 Caffeine
        val localValue: Any = CAFFEINE.get(getUniqueKey(key)) { k ->
            cache.get(key, type)
        }
        return localValue as? T
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(key: Any, valueLoader: Callable<T>): T {
        // 先查本地 Caffeine
        val localValue: Any = CAFFEINE.get(getUniqueKey(key)) { k ->
            cache.get(key, valueLoader)
        }
        return localValue as T
    }

    override fun put(key: Any, value: Any?) {
        // 先清除本地缓存（避免脏数据）
        CAFFEINE.invalidate(getUniqueKey(key))

        // 存 Redis
        cache.put(key, value)
    }

    override fun putIfAbsent(key: Any, value: Any?): Cache.ValueWrapper? {
        // 先清除本地缓存
        CAFFEINE.invalidate(getUniqueKey(key))

        return cache.putIfAbsent(key, value) as? Cache.ValueWrapper
    }

    override fun evict(key: Any) {
        evictIfPresent(key)
    }

    override fun evictIfPresent(key: Any): Boolean {
        // 同时清除本地和 Redis
        val evicted = cache.evictIfPresent(key)
        if (evicted) {
            CAFFEINE.invalidate(getUniqueKey(key))
        }
        return evicted
    }

    override fun clear() {
        // 同时清除本地和 Redis
        CAFFEINE.invalidateAll()
        cache.clear()
    }

    override fun invalidate(): Boolean {
        // Redis 清除
        val invalidated = cache.invalidate()
        if (invalidated) {
            // 同时清除本地
            CAFFEINE.invalidateAll()
        }
        return invalidated
    }
}
