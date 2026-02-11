package com.github.alphafoxz.foxden.domain.system.bo

import com.github.alphafoxz.foxden.common.core.validate.AddGroup
import com.github.alphafoxz.foxden.common.core.validate.EditGroup
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * 租户业务对象 sys_tenant
 *
 * @author Lion Li
 */
data class SysTenantBo(
    /**
     * id
     */
    @get:NotNull(message = "id不能为空", groups = [EditGroup::class])
    var id: Long? = null,

    /**
     * 租户编号
     */
    var tenantId: String? = null,

    /**
     * 联系人
     */
    @get:NotBlank(message = "联系人不能为空", groups = [AddGroup::class, EditGroup::class])
    @get:Size(min = 0, max = 20, message = "联系人长度不能超过{max}个字符")
    var contactUserName: String? = null,

    /**
     * 联系电话
     */
    @get:NotBlank(message = "联系电话不能为空", groups = [AddGroup::class, EditGroup::class])
    @get:Size(min = 0, max = 20, message = "联系电话长度不能超过{max}个字符")
    var contactPhone: String? = null,

    /**
     * 企业名称
     */
    @get:NotBlank(message = "企业名称不能为空", groups = [AddGroup::class, EditGroup::class])
    var companyName: String? = null,

    /**
     * 用户名（创建系统用户）
     */
    @get:NotBlank(message = "用户名不能为空", groups = [AddGroup::class])
    var username: String? = null,

    /**
     * 密码（创建系统用户）
     */
    @get:NotBlank(message = "密码不能为空", groups = [AddGroup::class])
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
     * 统一社会信用代码
     */
    var licenseNumber: String? = null,

    /**
     * 地址
     */
    var address: String? = null,

    /**
     * 域名
     */
    var domain: String? = null,

    /**
     * 企业简介
     */
    var intro: String? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 租户套餐编号
     */
    @get:NotNull(message = "租户套餐不能为空", groups = [AddGroup::class])
    var packageId: Long? = null,

    /**
     * 过期时间
     */
    var expireTime: java.time.LocalDateTime? = null,

    /**
     * 用户数量（-1不限制）
     */
    var accountCount: Long? = null,

    /**
     * 租户状态（0正常 1停用）
     */
    var status: String? = null,

    /**
     * 创建者
     */
    var createBy: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
