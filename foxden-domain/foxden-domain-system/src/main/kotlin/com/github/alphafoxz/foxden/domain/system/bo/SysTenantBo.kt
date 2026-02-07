package com.github.alphafoxz.foxden.domain.system.bo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 租户业务对象 sys_tenant
 *
 * @author Lion Li
 */
data class SysTenantBo(
    /**
     * 租户ID
     */
    var tenantId: String? = null,

    /**
     * 联系人
     */
    @get:NotBlank(message = "联系人不能为空")
    @get:Size(min = 0, max = 20, message = "联系人长度不能超过{max}个字符")
    var contactUserName: String? = null,

    /**
     * 联系电话
     */
    @get:NotBlank(message = "联系电话不能为空")
    @get:Size(min = 0, max = 20, message = "联系电话长度不能超过{max}个字符")
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
     * 用户密码
     */
    var password: String? = null,

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
     * 创建者
     */
    var createBy: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
