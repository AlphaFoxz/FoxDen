package com.github.alphafoxz.foxden.domain.tenant.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommInfo
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommTenant
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.time.LocalDateTime
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.hibernate.validator.constraints.Length

/**
 * 租户
 * 
 * @author wong
 */
@Entity
@Table(name = "sys_tenant")
interface SysTenant : CommDelFlag, CommId, CommInfo, CommTenant {
    /**
     * 联系人
     */
    @Column(name = "contact_user_name")
    @get:Length(max = 2147483647)
    val contactUserName: String?

    /**
     * 联系电话
     */
    @Column(name = "contact_phone")
    @get:Length(max = 2147483647)
    val contactPhone: String?

    /**
     * 企业名称
     */
    @Column(name = "company_name")
    @get:Length(max = 2147483647)
    val companyName: String

    /**
     * 统一社会信用代码
     */
    @Column(name = "license_number")
    @get:Length(max = 2147483647)
    val licenseNumber: String?

    /**
     * 地址
     */
    @Column(name = "address")
    @get:Length(max = 2147483647)
    val address: String?

    /**
     * 企业简介
     */
    @Column(name = "intro")
    @get:Length(max = 2147483647)
    val intro: String?

    /**
     * 域名
     */
    @Column(name = "domain")
    @get:Length(max = 2147483647)
    val domain: String?

    /**
     * 备注
     */
    @Column(name = "remark")
    @get:Length(max = 2147483647)
    val remark: String?

    /**
     * 租户套餐编号
     */
    @Column(name = "package_id")
    @get:Max(value = 9223372036854775807, message = "租户套餐编号不可大于9223372036854775807")
    @get:Min(value = 0, message = "租户套餐编号不可小于0")
    val packageId: Long?

    /**
     * 过期时间
     */
    @Column(name = "expire_time")
    val expireTime: LocalDateTime?

    /**
     * 用户数量（-1不限制）
     */
    @Column(name = "account_count")
    @get:Max(value = 2147483647, message = "用户数量（-1不限制）不可大于2147483647")
    @get:Min(value = 0, message = "用户数量（-1不限制）不可小于0")
    val accountCount: Int?

    /**
     * 租户状态（0正常 1停用）
     */
    @Column(name = "status")
    @get:Length(max = 1)
    val status: String
}
