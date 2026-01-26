package com.github.alphafoxz.foxden.domain.tenant.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommInfo
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommTenant
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.hibernate.validator.constraints.Length

/**
 * 租户套餐
 * 
 * @author wong
 */
@Entity
@Table(name = "sys_tenant_package")
interface SysTenantPackage : CommDelFlag, CommId, CommInfo, CommTenant {
    /**
     * 套餐名称
     */
    @Column(name = "package_name")
    @get:Length(max = 2147483647)
    val packageName: String

    /**
     * 关联菜单id
     */
    @Column(name = "menu_ids")
    @get:Length(max = 2147483647)
    val menuIds: String?

    /**
     * 备注
     */
    @Column(name = "remark")
    @get:Length(max = 2147483647)
    val remark: String?

    /**
     * 菜单树选择项是否关联显示
     */
    @Column(name = "menu_check_strictly")
    val menuCheckStrictly: Byte?

    /**
     * 状态（0正常 1停用）
     */
    @Column(name = "status")
    @get:Length(max = 1)
    val status: String
}
