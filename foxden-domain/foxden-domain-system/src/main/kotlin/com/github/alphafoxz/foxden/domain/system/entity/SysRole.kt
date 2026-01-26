package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommInfo
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommTenant
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.hibernate.validator.constraints.Length

/**
 * 角色信息
 * 
 * @author wong
 */
@Entity
@Table(name = "sys_role")
interface SysRole : CommDelFlag, CommId, CommInfo, CommTenant {
    /**
     * 角色名称
     */
    @Column(name = "role_name")
    @get:Length(max = 2147483647)
    val roleName: String

    /**
     * 角色权限字符串
     */
    @Column(name = "role_key")
    @get:Length(max = 2147483647)
    val roleKey: String

    /**
     * 显示顺序
     */
    @Column(name = "role_sort")
    @get:Max(value = 2147483647, message = "显示顺序不可大于2147483647")
    @get:Min(value = 0, message = "显示顺序不可小于0")
    val roleSort: Int?

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限 6：部门及以下或本人数据权限）
     */
    @Column(name = "data_scope")
    @get:Length(max = 1)
    val dataScope: String?

    /**
     * 菜单树选择项是否关联显示
     */
    @Column(name = "menu_check_strictly")
    val menuCheckStrictly: Byte?

    /**
     * 部门树选择项是否关联显示
     */
    @Column(name = "dept_check_strictly")
    val deptCheckStrictly: Byte?

    /**
     * 角色状态（0正常 1停用）
     */
    @Column(name = "status")
    @get:Length(max = 1)
    val status: String

    /**
     * 备注
     */
    @Column(name = "remark")
    @get:Length(max = 2147483647)
    val remark: String?
}
