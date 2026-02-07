package com.github.alphafoxz.foxden.app.admin.domain.vo

/**
 * 租户列表
 *
 * @author FoxDen Team
 */
data class TenantListVo(
    /**
     * 租户编号
     */
    var tenantId: String? = null,

    /**
     * 企业名称
     */
    var companyName: String? = null,

    /**
     * 域名
     */
    var domain: String? = null
)
