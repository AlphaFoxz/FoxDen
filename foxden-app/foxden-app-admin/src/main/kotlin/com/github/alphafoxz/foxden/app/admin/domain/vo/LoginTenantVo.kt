package com.github.alphafoxz.foxden.app.admin.domain.vo

/**
 * 登录租户对象
 *
 * @author FoxDen Team
 */
data class LoginTenantVo(
    /**
     * 租户开关
     */
    var tenantEnabled: Boolean? = null,

    /**
     * 租户对象列表
     */
    var voList: List<TenantListVo>? = null
)
