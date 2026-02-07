package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 角色表 sys_role
 */
@Entity
interface SysRole : CommDelFlag, CommId, CommInfo, CommTenant {
    /**
     * 角色名称
     */
    val roleName: String

    /**
     * 角色权限字符
     */
    val roleKey: String

    /**
     * 角色排序
     */
    val roleSort: Int

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限 6：部门及以下或本人数据权限）
     */
    val dataScope: String?

    /**
     * 菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）
     */
    val menuCheckStrictly: Boolean?

    /**
     * 部门树选择项是否关联显示（0：父子不互相关联显示 1：父子互相关联显示）
     */
    val deptCheckStrictly: Boolean?

    /**
     * 角色状态（0正常 1停用）
     */
    val status: String?

    /**
     * 菜单
     */
    @OnDissociate(DissociateAction.DELETE)
    @ManyToMany
    @JoinTable(name = "sys_role_menu")
    val menus: List<SysMenu>

    /**
     * 部门
     */
    @OnDissociate(DissociateAction.DELETE)
    @ManyToMany
    @JoinTable(name = "sys_role_dept")
    val depts: List<SysDept>
}
