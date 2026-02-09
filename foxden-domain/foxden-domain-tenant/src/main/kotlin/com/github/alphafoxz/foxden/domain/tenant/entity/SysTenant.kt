package com.github.alphafoxz.foxden.domain.tenant.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*
import java.util.Date

/**
 * 租户对象 sys_tenant
 */
@Entity
interface SysTenant : CommDelFlag, CommId, CommInfo {
    /**
     * 租户编号
     */
    val tenantId: String

    /**
     * 联系人
     */
    val contactUserName: String?

    /**
     * 联系电话
     */
    val contactPhone: String?

    /**
     * 企业名称
     */
    val companyName: String?

    /**
     * 统一社会信用代码
     */
    val licenseNumber: String?

    /**
     * 地址
     */
    val address: String?

    /**
     * 域名
     */
    val domain: String?

    /**
     * 企业简介
     */
    val intro: String?

    /**
     * 备注
     */
    val remark: String?

    /**
     * 租户套餐编号
     */
    val packageId: Long?

    /**
     * 过期时间
     */
    val expireTime: Date?

    /**
     * 用户数量（-1不限制）
     */
    val accountCount: Long?

    /**
     * 租户状态（0正常 1停用）
     */
    val status: String?

    /**
     * 租户套餐
     */
    @ManyToOne
    @JoinColumn(name = "package_id")
    val tenantPackage: SysTenantPackage?
}
