package com.github.alphafoxz.foxden.common.security.core.dao

import cn.dev33.satoken.dao.auto.SaTokenDaoBySessionFollowObject
import cn.dev33.satoken.util.SaFoxUtil
import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration
import java.util.ArrayList
import java.util.Collection
import java.util.concurrent.TimeUnit

/**
 * Sa-Token持久层接口(使用框架自带RedisUtils实现 协议统一)
 *
 * 采用 caffeine + redis 多级缓存 优化并发查询效率
 *
 * SaTokenDaoBySessionFollowObject 是 SaTokenDao 子集简化了session方法处理
 */
object FoxdenSaTokenDao : SaTokenDaoBySessionFollowObject {

    private val CAFFEINE: Cache<String, Any?> = Caffeine.newBuilder()
        // 设置最后一次写入或访问后经过固定时间过期
        .expireAfterWrite(5, TimeUnit.SECONDS)
        // 初始的缓存空间大小
        .initialCapacity(100)
        // 缓存的最大条数
        .maximumSize(1000)
        .build()

    /**
     * 获取Value，如无返空
     */
    override fun get(key: String): String? {
        val o = CAFFEINE.get(key) { k ->
            RedisUtils.getCacheObject<String>(k)
        }
        return o as? String
    }

    /**
     * 写入Value，并设定存活时间 (单位: 秒)
     */
    override fun set(key: String, value: String, timeout: Long) {
        if (timeout == 0L || timeout <= SaTokenDaoBySessionFollowObject.NOT_VALUE_EXPIRE) {
            return
        }
        // 判断是否为永不过期
        if (timeout == SaTokenDaoBySessionFollowObject.NEVER_EXPIRE) {
            RedisUtils.setCacheObject(key, value)
        } else {
            RedisUtils.setCacheObject(key, value, Duration.ofSeconds(timeout))
        }
        CAFFEINE.invalidate(key)
    }

    /**
     * 修改指定key-value键值对 (过期时间不变)
     */
    override fun update(key: String, value: String) {
        if (RedisUtils.hasKey(key)) {
            RedisUtils.setCacheObject(key, value, true)
            CAFFEINE.invalidate(key)
        }
    }

    /**
     * 删除Value
     */
    override fun delete(key: String) {
        if (RedisUtils.deleteObject(key)) {
            CAFFEINE.invalidate(key)
        }
    }

    /**
     * 获取Value的剩余存活时间 (单位: 秒)
     */
    override fun getTimeout(key: String): Long {
        val timeout = RedisUtils.getTimeToLive<Any>(key)
        // 加1的目的 解决sa-token使用秒 redis是毫秒导致1秒的精度问题 手动补偿
        return if (timeout < 0) timeout else timeout / 1000 + 1
    }

    /**
     * 修改Value的剩余存活时间 (单位: 秒)
     */
    override fun updateTimeout(key: String, timeout: Long) {
        RedisUtils.expire(key, Duration.ofSeconds(timeout))
    }

    /**
     * 获取Object，如无返空
     */
    override fun getObject(key: String): Any? {
        val o = CAFFEINE.get(key) { k ->
            RedisUtils.getCacheObject<Any>(k)
        }
        return o
    }

    /**
     * 获取 Object (指定反序列化类型)，如无返空
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> getObject(key: String, classType: Class<T>): T? {
        val o = CAFFEINE.get(key) { k ->
            RedisUtils.getCacheObject<Any>(k)
        }
        return o as? T
    }

    /**
     * 写入Object，并设定存活时间 (单位: 秒)
     */
    override fun setObject(key: String, `object`: Any?, timeout: Long) {
        if (timeout == 0L || timeout <= SaTokenDaoBySessionFollowObject.NOT_VALUE_EXPIRE) {
            return
        }
        // 判断是否为永不过期
        if (timeout == SaTokenDaoBySessionFollowObject.NEVER_EXPIRE) {
            RedisUtils.setCacheObject(key, `object`)
        } else {
            RedisUtils.setCacheObject(key, `object`, Duration.ofSeconds(timeout))
        }
        CAFFEINE.invalidate(key)
    }

    /**
     * 更新Object (过期时间不变)
     */
    override fun updateObject(key: String, `object`: Any?) {
        if (RedisUtils.hasKey(key)) {
            RedisUtils.setCacheObject(key, `object`, true)
            CAFFEINE.invalidate(key)
        }
    }

    /**
     * 删除Object
     */
    override fun deleteObject(key: String) {
        if (RedisUtils.deleteObject(key)) {
            CAFFEINE.invalidate(key)
        }
    }

    /**
     * 获取Object的剩余存活时间 (单位: 秒)
     */
    override fun getObjectTimeout(key: String): Long {
        val timeout = RedisUtils.getTimeToLive<Any>(key)
        // 加1的目的 解决sa-token使用秒 redis是毫秒导致1秒的精度问题 手动补偿
        return if (timeout < 0) timeout else timeout / 1000 + 1
    }

    /**
     * 修改Object的剩余存活时间 (单位: 秒)
     */
    override fun updateObjectTimeout(key: String, timeout: Long) {
        RedisUtils.expire(key, Duration.ofSeconds(timeout))
    }

    /**
     * 搜索数据
     */
    @Suppress("UNCHECKED_CAST")
    override fun searchData(prefix: String, keyword: String, start: Int, size: Int, sortType: Boolean): MutableList<String> {
        val keyStr = "$prefix*$keyword*"
        val result: Any? = CAFFEINE.get(keyStr) { k ->
            // Get keys and convert to Java ArrayList
            val keysColl = RedisUtils.keys(k)
            val javaList = java.util.ArrayList<String>()
            for (item in keysColl) {
                javaList.add(item)
            }
            SaFoxUtil.searchList(javaList, start, size, sortType)
        }
        @Suppress("UNCHECKED_CAST")
        val list = result as? java.util.List<String>
        return list?.toMutableList() ?: mutableListOf()
    }
}
