package com.github.alphafoxz.foxden.common.core.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * 租户配置属性
 *
 * @author FoxDen Team
 */
@Component
@ConfigurationProperties(prefix = "tenant")
class TenantProperties {
    /** 是否启用多租户功能 */
    var enable: Boolean = true

    /** 默认租户ID */
    var defaultTenantId: String = "000000"

    /** 超级管理员角色 key */
    var superAdminRoleKey: String = "superadmin"

    /** 租户管理员角色 key */
    var tenantAdminRoleKey: String = "admin"

    /** 租户管理员角色名称 */
    var tenantAdminRoleName: String = "管理员"
}
