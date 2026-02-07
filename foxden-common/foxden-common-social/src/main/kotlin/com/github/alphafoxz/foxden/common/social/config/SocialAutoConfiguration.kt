package com.github.alphafoxz.foxden.common.social.config

import com.github.alphafoxz.foxden.common.social.config.properties.SocialProperties
import com.github.alphafoxz.foxden.common.social.utils.AuthRedisStateCache
import me.zhyd.oauth.cache.AuthStateCache
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

/**
 * Social 自动配置
 *
 * @author FoxDen Team
 */
@AutoConfiguration
@EnableConfigurationProperties(SocialProperties::class)
class SocialAutoConfiguration {

    @Bean
    fun authStateCache(): AuthStateCache {
        return AuthRedisStateCache()
    }
}
