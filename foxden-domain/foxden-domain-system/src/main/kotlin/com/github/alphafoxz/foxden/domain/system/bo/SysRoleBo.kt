package com.github.alphafoxz.foxden.domain.system.bo

import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.validate.AddGroup
import com.github.alphafoxz.foxden.common.core.validate.EditGroup
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * 角色信息业务对象 sys_role
 *
 * @author Michelle.Chung
 */
data class SysRoleBo(
    /**
     * 角色ID
     */
    var roleId: Long? = null,

    /**
     * 角色名称
     */
    @get:NotBlank(message = "角色名称不能为空", groups = [AddGroup::class, EditGroup::class])
    @get:Size(min = 0, max = 30, message = "角色名称长度不能超过{max}个字符")
    var roleName: String? = null,

    /**
     * 角色权限字符串
     */
    @get:NotBlank(message = "角色权限字符串不能为空", groups = [AddGroup::class, EditGroup::class])
    @get:Size(min = 0, max = 100, message = "权限字符长度不能超过{max}个字符")
    var roleKey: String? = null,

    /**
     * 显示顺序
     */
    @get:NotNull(message = "显示顺序不能为空", groups = [AddGroup::class, EditGroup::class])
    var roleSort: Int? = null,

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限 6：部门及以下或本人数据权限）
     */
    var dataScope: String? = null,

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
     * 备注
     */
    var remark: String? = null,

    /**
     * 菜单组
     */
    var menuIds: Array<Long>? = null,

    /**
     * 部门组
     */
    var deptIds: Array<Long>? = null,

    /**
     * 创建者
     */
    var createBy: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null,

    /**
     * 更新者
     */
    var updateBy: String? = null,

    /**
     * 更新时间
     */
    var updateTime: java.time.LocalDateTime? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SysRoleBo) return false
        return roleId == other.roleId
    }

    override fun hashCode(): Int {
        return roleId?.hashCode() ?: 0
    }
}
