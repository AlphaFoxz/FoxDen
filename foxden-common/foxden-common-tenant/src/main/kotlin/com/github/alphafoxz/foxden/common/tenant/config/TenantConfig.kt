package com.github.alphafoxz.foxden.common.tenant.config

import com.github.alphafoxz.foxden.common.redis.config.properties.RedissonProperties
import com.github.alphafoxz.foxden.common.tenant.handler.TenantKeyPrefixHandler
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import org.slf4j.LoggerFactory
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean

/**
 * 租户配置类
 *
 * @author Lion Li
 */
@AutoConfiguration(after = [com.github.alphafoxz.foxden.common.redis.config.RedisConfig::class])
@ConditionalOnProperty(value = ["tenant.enable"], havingValue = "true", matchIfMissing = false)
class TenantConfig {

    private val log = LoggerFactory.getLogger(TenantConfig::class.java)

    /**
     * 配置 Redisson 使用租户前缀处理器
     */
    @Bean
    fun tenantRedissonCustomizer(properties: RedissonProperties): RedissonAutoConfigurationCustomizer {
        return RedissonAutoConfigurationCustomizer { config ->
            val nameMapper = TenantKeyPrefixHandler(properties.keyPrefix)

            // 使用反射获取单机配置
            val singleServerConfig = try {
                val method = config.javaClass.getDeclaredMethod("getSingleServerConfig")
                method.isAccessible = true
                method.invoke(config)
            } catch (e: Exception) {
                log.debug("无法获取单机配置: ${e.message}")
                null
            }

            // 设置单机模式的名称映射器
            if (singleServerConfig != null) {
                try {
                    val setNameMapperMethod = singleServerConfig.javaClass.getDeclaredMethod("setNameMapper", org.redisson.api.NameMapper::class.java)
                    setNameMapperMethod.invoke(singleServerConfig, nameMapper)
                } catch (e: Exception) {
                    log.warn("设置单机名称映射器失败: ${e.message}")
                }
            }

            // 使用反射获取集群配置
            val clusterServersConfig = try {
                val method = config.javaClass.getDeclaredMethod("getClusterServersConfig")
                method.isAccessible = true
                method.invoke(config)
            } catch (e: Exception) {
                log.debug("无法获取集群配置: ${e.message}")
                null
            }

            // 设置集群模式的名称映射器
            if (clusterServersConfig != null) {
                try {
                    val setNameMapperMethod = clusterServersConfig.javaClass.getDeclaredMethod("setNameMapper", org.redisson.api.NameMapper::class.java)
                    setNameMapperMethod.invoke(clusterServersConfig, nameMapper)
                } catch (e: Exception) {
                    log.warn("设置集群名称映射器失败: ${e.message}")
                }
            }

            log.info("初始化租户 Redis key 前缀处理器")
        }
    }
}
