package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import com.github.alphafoxz.foxden.common.web.core.BaseController
import org.springframework.web.bind.annotation.*

/**
 * 缓存监控
 *
 * @author FoxDen Team
 */
@RestController
@RequestMapping("/monitor/cache")
class CacheController : BaseController() {

    /**
     * 获取缓存监控信息
     */
    @SaCheckPermission("monitor:cache:list")
    @GetMapping()
    fun getInfo(): R<Map<String, Any>> {
        // RedisUtils.getInfo() 方法不存在，返回空信息
        val info = mapOf<String, Any>(
            "redis_version" to "unknown",
            "used_memory" to "unknown",
            "used_memory_human" to "unknown"
        )
        return R.ok(info)
    }

    /**
     * 获取缓存列表
     */
    @SaCheckPermission("monitor:cache:list")
    @GetMapping("/getNames")
    fun getCacheNames(): R<MutableSet<String>> {
        val keys = RedisUtils.keys("*")
        return R.ok(keys.toMutableSet())
    }

    /**
     * 获取缓存内容
     */
    @SaCheckPermission("monitor:cache:list")
    @GetMapping("/getKeys/{cacheName}")
    fun getCacheKeys(@PathVariable cacheName: String): R<Set<String>> {
        val keys = RedisUtils.keys("$cacheName*")
        return R.ok(keys.toSet())
    }

    /**
     * 获取缓存内容
     */
    @SaCheckPermission("monitor:cache:list")
    @GetMapping("/getValue/{cacheName}/{cacheKey}")
    fun getCacheValue(
        @PathVariable cacheName: String,
        @PathVariable cacheKey: String
    ): R<Any?> {
        return R.ok(RedisUtils.getCacheObject("$cacheName:$cacheKey"))
    }

    /**
     * 清理缓存名称
     */
    @SaCheckPermission("monitor:cache:remove")
    @Log(title = "缓存监控", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clearCacheName/{cacheName}")
    fun clearCacheName(@PathVariable cacheName: String): R<Void> {
        val keys = RedisUtils.keys("$cacheName*")
        keys.forEach { key ->
            RedisUtils.deleteObject(key)
        }
        return R.ok()
    }

    /**
     * 清理缓存键名
     */
    @SaCheckPermission("monitor:cache:remove")
    @Log(title = "缓存监控", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clearCacheKey/{cacheKey}")
    fun clearCacheKey(@PathVariable cacheKey: String): R<Void> {
        RedisUtils.deleteObject(cacheKey)
        return R.ok()
    }

    /**
     * 清理全部缓存
     */
    @SaCheckPermission("monitor:cache:remove")
    @Log(title = "缓存监控", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clearCacheAll")
    fun clearCacheAll(): R<Void> {
        val keys = RedisUtils.keys("*")
        keys.forEach { key ->
            RedisUtils.deleteObject(key)
        }
        return R.ok()
    }
}
