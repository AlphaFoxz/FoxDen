package com.github.alphafoxz.foxden.common.sse.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * SSE 配置属性
 *
 * @author FoxDen Team
 */
@ConfigurationProperties(prefix = "sse")
data class SseProperties(
    /**
     * 是否启用 SSE
     */
    var enabled: Boolean? = null,

    /**
     * SSE 连接路径
     */
    var path: String? = null
)
