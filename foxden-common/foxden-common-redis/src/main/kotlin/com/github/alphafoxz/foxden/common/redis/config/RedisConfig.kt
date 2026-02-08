package com.github.alphafoxz.foxden.common.redis.config

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.github.alphafoxz.foxden.common.redis.config.properties.RedissonProperties
import com.github.alphafoxz.foxden.common.redis.handler.KeyPrefixHandler
import org.redisson.client.codec.StringCodec
import org.redisson.codec.CompositeCodec
import org.redisson.codec.TypedJsonJacksonCodec
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.core.task.VirtualThreadTaskExecutor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

/**
 * redis配置
 */
@AutoConfiguration
@EnableConfigurationProperties(RedissonProperties::class)
class RedisConfig {
    private val log = LoggerFactory.getLogger(RedisConfig::class.java)

    @Bean
    fun redissonCustomizer(properties: RedissonProperties): RedissonAutoConfigurationCustomizer {
        return RedissonAutoConfigurationCustomizer { config ->
            val javaTimeModule = JavaTimeModule()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            javaTimeModule.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(formatter))
            javaTimeModule.addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(formatter))

            val om = ObjectMapper()
            om.registerModule(javaTimeModule)
            om.setTimeZone(TimeZone.getDefault())
            om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
            om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL)

            val jsonCodec = TypedJsonJacksonCodec(Any::class.java, om)
            // 组合序列化 key 使用 String 内容使用通用 json 格式
            val codec = CompositeCodec(StringCodec.INSTANCE, jsonCodec, jsonCodec)

            config.setThreads(properties.threads)
                .setNettyThreads(properties.nettyThreads)
                .setUseScriptCache(true) // 缓存 Lua 脚本
                .setCodec(codec)

            // 虚拟线程支持
            if (isVirtualThreadsEnabled()) {
                config.setNettyExecutor(VirtualThreadTaskExecutor("redisson-"))
            }

            // 单机配置
            properties.singleServerConfig?.let { single ->
                config.useSingleServer()
                    .setNameMapper(KeyPrefixHandler(properties.keyPrefix))
                    .apply {
                        single.address?.let { setAddress(it) }
                    }
                    .setTimeout(single.timeout)
                    .setClientName(single.clientName)
                    .setIdleConnectionTimeout(single.idleConnectionTimeout)
                    .setSubscriptionConnectionPoolSize(single.subscriptionConnectionPoolSize)
                    .setConnectionMinimumIdleSize(single.connectionMinimumIdleSize)
                    .setConnectionPoolSize(single.connectionPoolSize)
            }

            // 集群配置
            properties.clusterServersConfig?.let { cluster ->
                config.useClusterServers()
                    .setNameMapper(KeyPrefixHandler(properties.keyPrefix))
                    .setTimeout(cluster.timeout)
                    .setClientName(cluster.clientName)
                    .setIdleConnectionTimeout(cluster.idleConnectionTimeout)
                    .setSubscriptionConnectionPoolSize(cluster.subscriptionConnectionPoolSize)
                    // Note: Some Redisson cluster configuration methods have changed
                    // Using available methods for newer Redisson versions
            }

            log.info("初始化 redis 配置")
        }
    }

    private fun isVirtualThreadsEnabled(): Boolean {
        return try {
            Class.forName("java.lang.VirtualThread")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }
}
