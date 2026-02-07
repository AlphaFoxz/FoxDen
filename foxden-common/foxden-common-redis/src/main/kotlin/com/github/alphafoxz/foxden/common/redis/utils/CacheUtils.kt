package com.github.alphafoxz.foxden.common.redis.utils

import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager

/**
 * 缓存操作工具类
 */
object CacheUtils {

    private val CACHE_MANAGER: CacheManager = SpringUtils.getBean(CacheManager::class.java)

    /**
     * 获取缓存值
     *
     * @param cacheNames 缓存组名称
     * @param key        缓存key
     */
    @JvmStatic
    fun <T> get(cacheNames: String, key: Any): T? {
        val wrapper = CACHE_MANAGER.getCache(cacheNames)?.get(key)
        @Suppress("UNCHECKED_CAST")
        return wrapper?.get() as? T
    }

    /**
     * 保存缓存值
     *
     * @param cacheNames 缓存组名称
     * @param key        缓存key
     * @param value      缓存值
     */
    @JvmStatic
    fun put(cacheNames: String, key: Any, value: Any?) {
        value?.let { CACHE_MANAGER.getCache(cacheNames)?.put(key, it) }
    }

    /**
     * 删除缓存值
     *
     * @param cacheNames 缓存组名称
     * @param key        缓存key
     */
    @JvmStatic
    fun evict(cacheNames: String, key: Any) {
        CACHE_MANAGER.getCache(cacheNames)?.evict(key)
    }

    /**
     * 清空缓存值
     *
     * @param cacheNames 缓存组名称
     */
    @JvmStatic
    fun clear(cacheNames: String) {
        CACHE_MANAGER.getCache(cacheNames)?.clear()
    }
}
