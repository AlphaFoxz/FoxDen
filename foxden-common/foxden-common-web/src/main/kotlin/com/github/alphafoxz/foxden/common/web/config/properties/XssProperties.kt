package com.github.alphafoxz.foxden.common.web.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * XSS 过滤配置属性
 */
@ConfigurationProperties(prefix = "xss")
data class XssProperties(
    /**
     * XSS 开关
     */
    var enabled: Boolean? = null,

    /**
     * 排除路径
     */
    var excludeUrls: List<String> = mutableListOf()
)
