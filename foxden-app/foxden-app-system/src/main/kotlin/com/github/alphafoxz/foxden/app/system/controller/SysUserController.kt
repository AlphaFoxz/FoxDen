package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.core.utils.StreamUtils
import com.github.alphafoxz.foxden.common.excel.utils.ExcelUtil
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.helper.DataPermissionHelper
import com.github.alphafoxz.foxden.common.jimmer.helper.TenantHelper
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
import com.github.alphafoxz.foxden.domain.system.vo.SysUserInfoVo
import com.github.alphafoxz.foxden.domain.system.vo.UserInfoVo
import com.github.alphafoxz.foxden.domain.system.listener.SysUserImportListener
import com.github.alphafoxz.foxden.domain.system.vo.SysUserImportVo
import com.github.alphafoxz.foxden.common.core.domain.Tree
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
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getInfo")
    fun getInfo(): R<UserInfoVo> {
        val loginUser = LoginHelper.getLoginUser() ?: return R.fail("获取用户信息失败")

        // 超级管理员 如果重新加载用户信息需清除动态租户
        if (TenantHelper.isEnable() && LoginHelper.isSuperAdmin()) {
            TenantHelper.clearDynamic()
        }

        val user = DataPermissionHelper.ignore(java.util.function.Supplier {
            userService.selectUserById(loginUser.userId!!)
        })
        if (user == null) {
            return R.fail("没有权限访问用户数据!")
        }

        val userInfoVo = UserInfoVo(
            user = user,
            permissions = loginUser.menuPermission,
            roles = loginUser.rolePermission
        )
        return R.ok(userInfoVo)
    }

    /**
     * 根据用户编号获取详细信息
     *
     * @param userId 用户ID
     */
    @SaCheckPermission("system:user:query")
    @GetMapping(value = ["/", "/{userId}"])
    fun getInfo(@PathVariable(required = false) userId: Long?): R<SysUserInfoVo> {
        val userInfoVo = SysUserInfoVo()

        if (userId != null) {
            userService.checkUserDataScope(userId)
            val sysUser = userService.selectUserById(userId)
            userInfoVo.user = sysUser
            userInfoVo.roleIds = roleService.selectRoleListByUserId(userId)

            val deptId = sysUser?.deptId
            if (deptId != null) {
                // 查询部门下的岗位列表
                val postBo = com.github.alphafoxz.foxden.domain.system.bo.SysPostBo().apply {
                    this.deptId = deptId
                    this.status = SystemConstants.NORMAL
                }
                userInfoVo.posts = postService.selectPostList(postBo)
                // 查询用户的岗位ID列表
                userInfoVo.postIds = postService.selectPostsByUserId(userId)
            }
        }

        // 获取角色列表（状态正常）
        val roleBo = com.github.alphafoxz.foxden.domain.system.bo.SysRoleBo().apply {
            status = SystemConstants.NORMAL
        }
        val roles = roleService.selectRoleList(roleBo)
        userInfoVo.roles = if (LoginHelper.isSuperAdmin(userId)) {
            roles
        } else {
            StreamUtils.filter(roles) { r -> !r.isSuperAdmin() }
        }

        return R.ok(userInfoVo)
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

    /**
     * 获取部门树列表
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/deptTree")
    fun deptTree(@RequestParam(required = false) deptId: Long?): R<List<Tree<Long>>> {
        val depts = deptService.selectDeptTreeList(com.github.alphafoxz.foxden.domain.system.bo.SysDeptBo())
        return R.ok(depts)
    }

    /**
     * 下载用户导入模板
     */
    @SaCheckPermission("system:user:import")
    @PostMapping("/importTemplate")
    fun importTemplate(response: HttpServletResponse) {
        ExcelUtil.exportExcel(
            emptyList(),
            "用户数据",
            SysUserImportVo::class.java,
            response
        )
    }

    /**
     * 导入用户数据
     */
    @SaCheckPermission("system:user:import")
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @RepeatSubmit
    @PostMapping("/importData")
    fun importData(
        @RequestParam(required = false) isUpdateSupport: Boolean?,
        @RequestParam file: MultipartFile
    ): R<String> {
        val listener = SysUserImportListener(isUpdateSupport ?: false)
        val result = ExcelUtil.importExcel(
            file.inputStream,
            SysUserImportVo::class.java,
            listener
        )
        return R.ok(result.getAnalysis())
    }
}
