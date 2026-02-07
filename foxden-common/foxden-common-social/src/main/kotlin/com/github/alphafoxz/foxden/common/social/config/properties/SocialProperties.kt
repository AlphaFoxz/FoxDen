package com.github.alphafoxz.foxden.common.social.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Social 配置属性
 *
 * @author FoxDen Team
 */
@Component
@ConfigurationProperties(prefix = "justauth")
data class SocialProperties(
    /**
     * 授权类型
     */
    var type: Map<String, SocialLoginConfigProperties>? = null
)
