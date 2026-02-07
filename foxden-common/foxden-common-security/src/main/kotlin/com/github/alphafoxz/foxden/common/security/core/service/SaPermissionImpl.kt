package com.github.alphafoxz.foxden.common.security.core.service

import cn.dev33.satoken.stp.StpInterface
import com.github.alphafoxz.foxden.common.core.enums.UserType
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.service.PermissionService
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper

/**
 * Sa-Token 权限管理实现类
 */
class SaPermissionImpl : StpInterface {

    /**
     * 获取菜单权限列表
     */
    override fun getPermissionList(loginId: Any, loginType: String): List<String> {
        val loginUser = LoginHelper.getLoginUser()
        if (loginUser == null || loginUser.loginId != loginId) {
            val permissionService = getPermissionService()
            if (permissionService != null) {
                val list = StringUtils.splitList(loginId.toString(), ":")
                return permissionService.getMenuPermission(list[1].toLong()).toList()
            } else {
                throw ServiceException("PermissionService 实现类不存在")
            }
        }
        val userType = UserType.getUserType(loginUser.userType)
        if (userType == UserType.APP_USER) {
            // 其他端 自行根据业务编写
        }
        return if (loginUser.menuPermission.isNotEmpty()) {
            loginUser.menuPermission.toList()
        } else {
            emptyList()
        }
    }

    /**
     * 获取角色权限列表
     */
    override fun getRoleList(loginId: Any, loginType: String): List<String> {
        val loginUser = LoginHelper.getLoginUser()
        if (loginUser == null || loginUser.loginId != loginId) {
            val permissionService = getPermissionService()
            if (permissionService != null) {
                val list = StringUtils.splitList(loginId.toString(), ":")
                return permissionService.getRolePermission(list[1].toLong()).toList()
            } else {
                throw ServiceException("PermissionService 实现类不存在")
            }
        }
        val userType = UserType.getUserType(loginUser.userType)
        if (userType == UserType.APP_USER) {
            // 其他端 自行根据业务编写
        }
        return if (loginUser.rolePermission.isNotEmpty()) {
            loginUser.rolePermission.toList()
        } else {
            emptyList()
        }
    }

    private fun getPermissionService(): PermissionService? {
        return try {
            SpringUtils.getBean(PermissionService::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
