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
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysUserBo
import com.github.alphafoxz.foxden.domain.system.service.SysDeptService
import com.github.alphafoxz.foxden.domain.system.service.SysPostService
import com.github.alphafoxz.foxden.domain.system.service.SysRoleService
import com.github.alphafoxz.foxden.domain.system.service.SysTenantService
import com.github.alphafoxz.foxden.domain.system.service.SysUserService
import com.github.alphafoxz.foxden.domain.system.vo.SysUserExportVo
import com.github.alphafoxz.foxden.domain.system.vo.SysUserVo
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * 用户信息
 *
 * @author FoxDen Team
 */
@Validated
@RestController
@RequestMapping("/system/user")
class SysUserController(
    private val userService: SysUserService,
    private val roleService: SysRoleService,
    private val postService: SysPostService,
    private val deptService: SysDeptService,
    private val tenantService: SysTenantService
) : BaseController() {

    /**
     * 获取用户列表
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/list")
    fun list(user: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
        return userService.selectPageUserList(user, pageQuery)
    }

    /**
     * 导出用户列表
     */
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:user:export")
    @PostMapping("/export")
    fun export(user: SysUserBo, response: HttpServletResponse) {
        val list = userService.selectUserExportList(user)
        ExcelUtil.exportExcel(list, "用户数据", SysUserExportVo::class.java, response)
    }

    /**
     * 根据用户编号获取详细信息
     */
    @SaCheckPermission("system:user:query")
    @GetMapping("/{userId}")
    fun getInfo(@PathVariable userId: Long): R<SysUserVo> {
        userService.checkUserDataScope(userId)
        return R.ok(userService.selectUserById(userId))
    }

    /**
     * 新增用户
     */
    @SaCheckPermission("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(@Validated @RequestBody user: SysUserBo): R<Void> {
        userService.checkUserAllowed(user.userId ?: 0L)
        userService.checkUserNameUnique(user)
        userService.checkEmailUnique(user)
        userService.checkPhoneUnique(user)
        return toAjax(userService.insertUser(user))
    }

    /**
     * 修改用户
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(@Validated @RequestBody user: SysUserBo): R<Void> {
        userService.checkUserAllowed(user.userId!!)
        userService.checkUserDataScope(user.userId!!)
        userService.checkUserNameUnique(user)
        userService.checkEmailUnique(user)
        userService.checkPhoneUnique(user)
        return toAjax(userService.updateUser(user))
    }

    /**
     * 删除用户
     */
    @SaCheckPermission("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    fun remove(@PathVariable userIds: Array<Long>): R<Void> {
        userIds.forEach { userId ->
            userService.checkUserDataScope(userId)
        }
        return toAjax(userService.deleteUserByIds(userIds))
    }

    /**
     * 重置密码
     */
    @SaCheckPermission("system:user:resetPwd")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    fun resetPwd(@RequestBody user: SysUserBo): R<Void> {
        userService.checkUserAllowed(user.userId!!)
        userService.checkUserDataScope(user.userId!!)
        return toAjax(userService.resetUserPwd(user.userId!!, user.password ?: ""))
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    fun changeStatus(@RequestBody user: SysUserBo): R<Void> {
        userService.checkUserAllowed(user.userId!!)
        userService.checkUserDataScope(user.userId!!)
        return toAjax(userService.updateUserStatus(user.userId!!, user.status!!))
    }

    /**
     * 根据角色ID查询用户列表
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/authRole/{roleId}")
    fun listAuthRole(@PathVariable roleId: Long): R<List<SysUserVo>> {
        val users = userService.selectUserByIds(listOf(roleId), null)
        return R.ok(users)
    }

    /**
     * 根据部门ID查询用户列表
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/dept/{deptId}")
    fun listByDeptId(@PathVariable deptId: Long): R<List<SysUserVo>> {
        return R.ok(userService.selectUserListByDept(deptId))
    }
}
