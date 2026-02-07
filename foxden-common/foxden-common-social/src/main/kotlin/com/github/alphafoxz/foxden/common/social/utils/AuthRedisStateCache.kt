package com.github.alphafoxz.foxden.common.social.utils

import com.github.alphafoxz.foxden.common.core.constant.GlobalConstants
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import me.zhyd.oauth.cache.AuthStateCache
import java.time.Duration

/**
 * 授权状态缓存
 *
 * @author FoxDen Team
 */
class AuthRedisStateCache : AuthStateCache {

    /**
     * 存入缓存
     *
     * @param key   缓存key
     * @param value 缓存内容
     */
    override fun cache(key: String, value: String) {
        // 授权超时时间 默认三分钟
        RedisUtils.setCacheObject(
            GlobalConstants.SOCIAL_AUTH_CODE_KEY + key,
            value,
            Duration.ofMinutes(3)
        )
    }

    /**
     * 存入缓存
     *
     * @param key     缓存key
     * @param value   缓存内容
     * @param timeout 指定缓存过期时间(毫秒)
     */
    override fun cache(key: String, value: String, timeout: Long) {
        RedisUtils.setCacheObject(
            GlobalConstants.SOCIAL_AUTH_CODE_KEY + key,
            value,
            Duration.ofMillis(timeout)
        )
    }

    /**
     * 获取缓存内容
     *
     * @param key 缓存key
     * @return 缓存内容
     */
    override fun get(key: String): String? {
        return RedisUtils.getCacheObject(GlobalConstants.SOCIAL_AUTH_CODE_KEY + key)
    }

    /**
     * 是否存在key，如果对应key的value值已过期，也返回false
     *
     * @param key 缓存key
     * @return true：存在key，并且value没过期；false：key不存在或者已过期
     */
    override fun containsKey(key: String): Boolean {
        return RedisUtils.hasKey(GlobalConstants.SOCIAL_AUTH_CODE_KEY + key)
    }
}
