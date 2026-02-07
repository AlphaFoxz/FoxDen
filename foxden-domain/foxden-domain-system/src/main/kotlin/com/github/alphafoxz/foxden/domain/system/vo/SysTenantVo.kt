package com.github.alphafoxz.foxden.domain.system.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 租户视图对象 sys_tenant
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SysTenantVo(
    /**
     * 租户ID
     */
    var tenantId: String? = null,

    /**
     * 联系人
     */
    var contactUserName: String? = null,

    /**
     * 联系电话
     */
    var contactPhone: String? = null,

    /**
     * 企业名称
     */
    var companyName: String? = null,

    /**
     * 用户名（租户管理员账号）
     */
    var userName: String? = null,

    /**
     * 用户昵称
     */
    var nickName: String? = null,

    /**
     * 用户邮箱
     */
    var email: String? = null,

    /**
     * 租户套餐ID
     */
    var packageId: Long? = null,

    /**
     * 租户套餐名称
     */
    var packageName: String? = null,

    /**
     * 过期时间
     */
    var expireTime: java.time.LocalDateTime? = null,

    /**
     * 用户数量
     */
    var accountCount: Int? = null,

    /**
     * 租户状态（0正常 1停用）
     */
    var status: String? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
