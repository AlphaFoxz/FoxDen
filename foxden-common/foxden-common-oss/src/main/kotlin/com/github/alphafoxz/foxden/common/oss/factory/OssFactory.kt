package com.github.alphafoxz.foxden.common.oss.factory

import com.github.alphafoxz.foxden.common.core.constant.CacheNames
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
import com.github.alphafoxz.foxden.common.redis.utils.CacheUtils
import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import com.github.alphafoxz.foxden.common.oss.constant.OssConstant
import com.github.alphafoxz.foxden.common.oss.core.OssClient
import com.github.alphafoxz.foxden.common.oss.exception.OssException
import com.github.alphafoxz.foxden.common.oss.properties.OssProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

/**
 * 文件上传Factory
 */
@Configuration
class OssFactory {
    private val log = LoggerFactory.getLogger(OssFactory::class.java)

    companion object {
        private val CLIENT_CACHE = ConcurrentHashMap<String, OssClient>()
        private val LOCK = ReentrantLock()

        /**
         * 获取默认实例
         */
        @JvmStatic
        fun instance(): OssClient {
            // 获取redis默认类型
            val configKey = RedisUtils.getCacheObject<String>(OssConstant.DEFAULT_CONFIG_KEY)
            if (StringUtils.isEmpty(configKey)) {
                throw OssException("文件存储服务类型无法找到!")
            }
            return instance(configKey!!)
        }

        /**
         * 根据类型获取实例
         */
        @JvmStatic
        fun instance(configKey: String): OssClient {
            val json = CacheUtils.get<String>(CacheNames.SYS_OSS_CONFIG, configKey)
            if (json == null) {
                throw OssException("系统异常, '$configKey'配置信息不存在!")
            }
            val properties = JsonUtils.parseObject(json, OssProperties::class.java)
                ?: throw OssException("系统异常, '$configKey'配置信息解析失败!")
            // 使用租户标识避免多个租户相同key实例覆盖
            val tenantId = properties.tenantId
            val key = if (StringUtils.isNotBlank(tenantId)) {
                "$tenantId:$configKey"
            } else {
                configKey
            }

            var client = CLIENT_CACHE[key]
            // 客户端不存在或配置不相同则重新构建
            if (client == null || !client.checkPropertiesSame(properties)) {
                LOCK.lock()
                try {
                    client = CLIENT_CACHE[key]
                    if (client == null || !client.checkPropertiesSame(properties)) {
                        val newClient = OssClient(configKey, properties)
                        CLIENT_CACHE[key] = newClient
                        val log = LoggerFactory.getLogger(OssFactory::class.java)
                        log.info("创建OSS实例 key => {}", configKey)
                        return newClient
                    }
                } finally {
                    LOCK.unlock()
                }
            }
            return client
        }
    }
}
