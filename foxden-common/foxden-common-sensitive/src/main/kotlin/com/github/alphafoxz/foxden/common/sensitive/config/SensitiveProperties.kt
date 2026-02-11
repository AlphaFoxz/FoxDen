package com.github.alphafoxz.foxden.common.sensitive.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 数据脱敏配置属性
 *
 * @author Lion Li
 */
@ConfigurationProperties(prefix = "foxden.sensitive")
data class SensitiveProperties(
    /**
     * 是否启用数据脱敏
     */
    val enabled: Boolean = true,

    /**
     * 超级管理员角色标识
     */
    val superAdminRole: String = "admin",

    /**
     * 超级管理员权限标识
     */
    val superAdminPerms: String = "*:*:*"
)
