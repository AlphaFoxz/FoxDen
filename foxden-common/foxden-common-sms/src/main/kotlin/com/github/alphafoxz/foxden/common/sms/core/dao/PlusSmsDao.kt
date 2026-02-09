package com.github.alphafoxz.foxden.common.sms.core.dao

import com.github.alphafoxz.foxden.common.core.constant.GlobalConstants
import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import org.dromara.sms4j.api.dao.SmsDao
import java.time.Duration

/**
 * SmsDao缓存配置 (使用框架自带RedisUtils实现 协议统一)
 * <p>主要用于短信重试和拦截的缓存
 *
 * @author Feng
 */
class PlusSmsDao : SmsDao {

    /**
     * 存储
     *
     * @param key       键
     * @param value     值
     * @param cacheTime 缓存时间（单位：秒)
     */
    override fun set(key: String, value: Any, cacheTime: Long) {
        RedisUtils.setCacheObject(GlobalConstants.GLOBAL_REDIS_KEY + key, value, Duration.ofSeconds(cacheTime))
    }

    /**
     * 存储
     *
     * @param key   键
     * @param value 值
     */
    override fun set(key: String, value: Any) {
        RedisUtils.setCacheObject(GlobalConstants.GLOBAL_REDIS_KEY + key, value, true)
    }

    /**
     * 读取
     *
     * @param key 键
     * @return 值
     */
    override fun get(key: String): Any? {
        return RedisUtils.getCacheObject(GlobalConstants.GLOBAL_REDIS_KEY + key)
    }

    /**
     * remove
     * <p> 根据key移除缓存
     *
     * @param key 缓存键
     * @return 被删除的value
     * @author :Wind
     */
    override fun remove(key: String): Any? {
        return RedisUtils.deleteObject(GlobalConstants.GLOBAL_REDIS_KEY + key)
    }

    /**
     * 清空
     */
    override fun clean() {
        RedisUtils.deleteKeys(GlobalConstants.GLOBAL_REDIS_KEY + "sms:*")
    }
}
