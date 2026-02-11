package com.github.alphafoxz.foxden.common.sensitive.impl

import com.github.alphafoxz.foxden.common.sensitive.config.SensitiveProperties
import com.github.alphafoxz.foxden.common.sensitive.core.SensitiveService
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper

/**
 * 默认数据脱敏服务实现
 * 默认管理员不过滤
 *
 * @author Lion Li
 */
class DefaultSensitiveServiceImpl(
    private val properties: SensitiveProperties
) : SensitiveService {

    override fun isSensitive(roleKey: Array<String>, perms: Array<String>): Boolean {
        if (!properties.enabled) {
            return false
        }

        try {
            // 获取当前登录用户
            val loginUser = LoginHelper.getLoginUser() ?: return false

            // 超级管理员不脱敏
            if (LoginHelper.isSuperAdmin(loginUser.userId)) {
                return false
            }

            // 检查角色
            if (roleKey.isNotEmpty()) {
                val rolePermissions = loginUser.rolePermission
                val hasRole = roleKey.any { it in rolePermissions }
                if (hasRole) {
                    return false // 有指定角色不脱敏
                }
            }

            // 检查权限
            if (perms.isNotEmpty()) {
                val menuPermissions = loginUser.menuPermission
                val hasPermission = perms.any { it in menuPermissions }
                if (hasPermission) {
                    return false // 有指定权限不脱敏
                }
            }

            // 默认进行脱敏
            return true
        } catch (e: Exception) {
            // 出现异常时不脱敏，避免影响正常业务
            return false
        }
    }
}
