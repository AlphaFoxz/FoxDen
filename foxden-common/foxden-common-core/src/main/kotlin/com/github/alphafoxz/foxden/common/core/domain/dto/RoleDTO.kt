package com.github.alphafoxz.foxden.common.core.domain.dto

import java.io.Serializable
import java.time.LocalDateTime

/**
 * 角色 DTO
 */
data class RoleDTO(
    /**
     * 角色ID
     */
    var roleId: Long? = null,

    /**
     * 角色名称
     */
    var roleName: String? = null,

    /**
     * 角色权限
     */
    var roleKey: String? = null,

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限 6：部门及以下或本人数据权限）
     */
    var dataScope: String? = null,

    /**
     * 显示顺序
     */
    var roleSort: Int? = null,

    /**
     * 菜单树选择项是否关联显示
     */
    var menuCheckStrictly: Boolean? = null,

    /**
     * 部门树选择项是否关联显示
     */
    var deptCheckStrictly: Boolean? = null,

    /**
     * 角色状态（0正常 1停用）
     */
    var status: String? = null,

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    var delFlag: String? = null,

    /**
     * 创建者
     */
    var createBy: String? = null,

    /**
     * 创建时间
     */
    var createTime: LocalDateTime? = null,

    /**
     * 更新者
     */
    var updateBy: String? = null,

    /**
     * 更新时间
     */
    var updateTime: LocalDateTime? = null,

    /**
     * 备注
     */
    var remark: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
