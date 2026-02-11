package com.github.alphafoxz.foxden.domain.system.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 角色信息视图对象 sys_role
 *
 * @author Michelle.Chung
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ExcelIgnoreUnannotated
data class SysRoleVo(
    /**
     * 角色ID
     */
    @ExcelProperty(value = ["角色序号"])
    var roleId: Long? = null,

    /**
     * 角色名称
     */
    @ExcelProperty(value = ["角色名称"])
    var roleName: String? = null,

    /**
     * 角色权限字符串
     */
    @ExcelProperty(value = ["角色权限"])
    var roleKey: String? = null,

    /**
     * 显示顺序
     */
    @ExcelProperty(value = ["角色排序"])
    var roleSort: Int? = null,

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限）
     */
    @ExcelProperty(value = ["数据范围"], converter = com.github.alphafoxz.foxden.common.excel.convert.ExcelDictConvert::class)
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
    @ExcelProperty(value = ["备注"])
    var remark: String? = null,

    /**
     * 菜单组
     */
    var menuIds: List<Long>? = null,

    /**
     * 部门组
     */
    var deptIds: List<Long>? = null,

    /**
     * 角色菜单列表
     */
    var menus: List<SysMenuVo>? = null,

    /**
     * 创建时间
     */
    @ExcelProperty(value = ["创建时间"])
    var createTime: java.time.LocalDateTime? = null,

    /**
     * 创建者
     */
    var createBy: String? = null,

    /**
     * 更新时间
     */
    var updateTime: java.time.LocalDateTime? = null,

    /**
     * 更新者
     */
    var updateBy: String? = null,

    /**
     * 标志位（0代表存在 1代表删除）
     */
    var delFlag: String? = null,

    /**
     * 用户是否存在此角色标识
     */
    var flag: Boolean? = null
) {
    /**
     * 是否为超级管理员角色
     */
    fun isSuperAdmin(): Boolean {
        return com.github.alphafoxz.foxden.common.core.constant.SystemConstants.SUPER_ADMIN_ID == roleId
    }
}
