package com.github.alphafoxz.foxden.domain.tenant.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 租户套餐对象 sys_tenant_package
 */
@Entity
interface SysTenantPackage : CommDelFlag, CommId, CommInfo {
    /**
     * 套餐名称
     */
    val packageName: String

    /**
     * 关联菜单id
     */
    val menuIds: String?

    /**
     * 菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）
     */
    val menuCheckStrictly: Boolean?

    /**
     * 状态（0正常 1停用）
     */
    val status: String?

    /**
     * 租户
     */
    @OnDissociate(DissociateAction.DELETE)
    @OneToMany(mappedBy = "tenantPackage")
    val tenants: List<SysTenant>
}
