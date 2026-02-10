package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.excel.utils.ExcelUtil
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysDeptBo
import com.github.alphafoxz.foxden.domain.system.bo.SysRoleBo
import com.github.alphafoxz.foxden.domain.system.bo.SysUserBo
import com.github.alphafoxz.foxden.domain.system.service.SysDeptService
import com.github.alphafoxz.foxden.domain.system.service.SysRoleService
import com.github.alphafoxz.foxden.domain.system.service.SysUserService
import com.github.alphafoxz.foxden.domain.system.vo.SysRoleVo
import com.github.alphafoxz.foxden.domain.system.vo.SysUserVo
import jakarta.servlet.http.HttpServletResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 角色信息
 *
 * @author FoxDen Team
 */
@Validated
@RestController
@RequestMapping("/system/role")
class SysRoleController(
    private val roleService: SysRoleService,
    private val userService: SysUserService,
    private val deptService: SysDeptService
) : BaseController() {

    /**
     * 获取角色信息列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/list")
    fun list(role: SysRoleBo, pageQuery: PageQuery): TableDataInfo<SysRoleVo> {
        return roleService.selectPageRoleList(role, pageQuery)
    }

    /**
     * 导出角色信息列表
     */
    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:role:export")
    @PostMapping("/export")
    fun export(role: SysRoleBo, response: HttpServletResponse) {
        val list = roleService.selectRoleList(role)
        ExcelUtil.exportExcel(list, "角色数据", SysRoleVo::class.java, response)
    }

    /**
     * 根据角色编号获取详细信息
     */
    @SaCheckPermission("system:role:query")
    @GetMapping("/{roleId}")
    fun getInfo(@PathVariable roleId: Long): R<SysRoleVo> {
        roleService.checkRoleDataScope(roleId)
        return R.ok(roleService.selectRoleById(roleId))
    }

    /**
     * 新增角色
     */
    @SaCheckPermission("system:role:add")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(@Validated @RequestBody role: SysRoleBo): R<Void> {
        roleService.checkRoleAllowed(role)
        if (!roleService.checkRoleNameUnique(role)) {
            return R.fail("新增角色'" + role.roleName + "'失败，角色名称已存在")
        } else if (!roleService.checkRoleKeyUnique(role)) {
            return R.fail("新增角色'" + role.roleName + "'失败，角色权限已存在")
        }
        return toAjax(roleService.insertRole(role))
    }

    /**
     * 修改保存角色
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(@Validated @RequestBody role: SysRoleBo): R<Void> {
        roleService.checkRoleAllowed(role)
        roleService.checkRoleDataScope(role.roleId!!)
        if (!roleService.checkRoleNameUnique(role)) {
            return R.fail("修改角色'" + role.roleName + "'失败，角色名称已存在")
        } else if (!roleService.checkRoleKeyUnique(role)) {
            return R.fail("修改角色'" + role.roleName + "'失败，角色权限已存在")
        }
        return toAjax(roleService.updateRole(role))
    }

    /**
     * 修改保存数据权限
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/dataScope")
    fun dataScope(@RequestBody role: SysRoleBo): R<Void> {
        roleService.checkRoleAllowed(role)
        roleService.checkRoleDataScope(role.roleId!!)
        return toAjax(roleService.authDataScope(role))
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    fun changeStatus(@RequestBody role: SysRoleBo): R<Void> {
        roleService.checkRoleAllowed(role)
        roleService.checkRoleDataScope(role.roleId ?: 0L)
        return toAjax(roleService.updateRoleStatus(role.roleId ?: 0L, role.status ?: ""))
    }

    /**
     * 删除角色
     */
    @SaCheckPermission("system:role:remove")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    fun remove(@PathVariable roleIds: Array<Long>): R<Void> {
        return toAjax(roleService.deleteRoleByIds(roleIds.toList()))
    }

    /**
     * 获取角色选择框列表
     */
    @SaCheckPermission("system:role:query")
    @GetMapping("/optionselect")
    fun optionselect(): R<List<SysRoleVo>> {
        return R.ok(roleService.selectRoleAll())
    }

    /**
     * 查询已分配用户角色列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/authUser/allocatedList")
    fun allocatedList(user: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
        return userService.selectAllocatedList(user, pageQuery)
    }

    /**
     * 查询未分配用户角色列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/authUser/unallocatedList")
    fun unallocatedList(user: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
        return userService.selectUnallocatedList(user, pageQuery)
    }

    /**
     * 取消授权用户
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @RepeatSubmit
    @PutMapping("/authUser/cancel")
    fun cancelAuthUser(@RequestBody userRole: UserRoleRequest): R<Void> {
        return toAjax(roleService.deleteAuthUser(userRole.userId, userRole.roleId))
    }

    /**
     * 用户角色请求对象
     */
    data class UserRoleRequest(
        val userId: Long,
        val roleId: Long
    )

    /**
     * 批量取消授权用户
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @RepeatSubmit
    @PutMapping("/authUser/cancelAll")
    fun cancelAuthUserAll(@RequestParam roleId: Long, @RequestParam userIds: Array<Long>): R<Void> {
        return toAjax(roleService.deleteAuthUsers(roleId, userIds))
    }

    /**
     * 批量选择用户授权
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @RepeatSubmit
    @PutMapping("/authUser/selectAll")
    fun selectAuthUserAll(@RequestParam roleId: Long, @RequestParam userIds: Array<Long>): R<Void> {
        roleService.checkRoleDataScope(roleId)
        return toAjax(roleService.insertAuthUsers(roleId, userIds))
    }

    /**
     * 获取对应角色部门树列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/deptTree/{roleId}")
    fun roleDeptTreeselect(@PathVariable roleId: Long): R<DeptTreeSelectVo> {
        val checkedKeys = deptService.selectDeptListByRoleId(roleId)
        val depts = deptService.selectDeptTreeList(SysDeptBo())
        return R.ok(DeptTreeSelectVo(checkedKeys, depts))
    }

    /**
     * 角色部门树选择VO
     */
    data class DeptTreeSelectVo(
        val checkedKeys: List<Long>,
        val depts: List<com.github.alphafoxz.foxden.common.core.domain.Tree<Long>>
    )
}
