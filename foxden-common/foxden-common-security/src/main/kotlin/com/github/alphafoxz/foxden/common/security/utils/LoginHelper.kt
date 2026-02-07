package com.github.alphafoxz.foxden.common.security.utils

import cn.dev33.satoken.session.SaSession
import cn.dev33.satoken.stp.StpUtil
import cn.dev33.satoken.stp.parameter.SaLoginParameter
import cn.hutool.core.convert.Convert
import cn.hutool.core.util.ObjectUtil
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.constant.TenantConstants
import com.github.alphafoxz.foxden.common.core.domain.model.LoginUser
import com.github.alphafoxz.foxden.common.core.enums.UserType

/**
 * 登录鉴权助手
 *
 * user_type 为 用户类型 同一个用户表 可以有多种用户类型 例如 pc,app
 * device 为 设备类型 同一个用户类型 可以有 多种设备类型 例如 web,ios
 * 可以组成 用户类型与设备类型多对多的 权限灵活控制
 *
 * 多用户体系 针对 多种用户类型 但权限控制不一致
 * 可以组成 多用户类型表与多设备类型 分别控制权限
 */
object LoginHelper {
    const val LOGIN_USER_KEY = "loginUser"
    const val TENANT_KEY = "tenantId"
    const val USER_KEY = "userId"
    const val USER_NAME_KEY = "userName"
    const val DEPT_KEY = "deptId"
    const val DEPT_NAME_KEY = "deptName"
    const val DEPT_CATEGORY_KEY = "deptCategory"
    const val CLIENT_KEY = "clientid"

    /**
     * 登录系统 基于 设备类型
     * 针对相同用户体系不同设备
     *
     * @param loginUser 登录用户信息
     * @param model     配置参数
     */
    @JvmStatic
    @JvmOverloads
    fun login(loginUser: LoginUser, model: SaLoginParameter? = null) {
        val finalModel = model ?: SaLoginParameter()
        StpUtil.login(
            loginUser.loginId,
            finalModel.setExtra(TENANT_KEY, loginUser.tenantId)
                .setExtra(USER_KEY, loginUser.userId)
                .setExtra(USER_NAME_KEY, loginUser.username)
                .setExtra(DEPT_KEY, loginUser.deptId)
                .setExtra(DEPT_NAME_KEY, loginUser.deptName)
                .setExtra(DEPT_CATEGORY_KEY, loginUser.deptCategory)
        )
        StpUtil.getTokenSession().set(LOGIN_USER_KEY, loginUser)
    }

    /**
     * 获取用户(多级缓存)
     */
    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun getLoginUser(): LoginUser? {
        val session = StpUtil.getTokenSession()
        if (ObjectUtil.isNull(session)) {
            return null
        }
        return session.get(LOGIN_USER_KEY) as? LoginUser
    }

    /**
     * 获取用户基于token
     */
    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun getLoginUser(token: String): LoginUser? {
        val session = StpUtil.getTokenSessionByToken(token)
        if (ObjectUtil.isNull(session)) {
            return null
        }
        return session.get(LOGIN_USER_KEY) as? LoginUser
    }

    /**
     * 获取用户id
     */
    @JvmStatic
    fun getUserId(): Long? {
        return Convert.toLong(getExtra(USER_KEY))
    }

    /**
     * 获取用户id
     */
    @JvmStatic
    fun getUserIdStr(): String? {
        return Convert.toStr(getExtra(USER_KEY))
    }

    /**
     * 获取用户账户
     */
    @JvmStatic
    fun getUsername(): String? {
        return Convert.toStr(getExtra(USER_NAME_KEY))
    }

    /**
     * 获取租户ID
     */
    @JvmStatic
    fun getTenantId(): String? {
        return Convert.toStr(getExtra(TENANT_KEY))
    }

    /**
     * 获取部门ID
     */
    @JvmStatic
    fun getDeptId(): Long? {
        return Convert.toLong(getExtra(DEPT_KEY))
    }

    /**
     * 获取部门名
     */
    @JvmStatic
    fun getDeptName(): String? {
        return Convert.toStr(getExtra(DEPT_NAME_KEY))
    }

    /**
     * 获取部门类别编码
     */
    @JvmStatic
    fun getDeptCategory(): String? {
        return Convert.toStr(getExtra(DEPT_CATEGORY_KEY))
    }

    /**
     * 获取当前 Token 的扩展信息
     *
     * @param key 键值
     * @return 对应的扩展数据
     */
    @JvmStatic
    private fun getExtra(key: String): Any? {
        return try {
            StpUtil.getExtra(key)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 获取用户类型
     */
    @JvmStatic
    fun getUserType(): UserType? {
        val loginType = StpUtil.getLoginIdAsString() ?: return null
        return UserType.getUserType(loginType)
    }

    /**
     * 是否为超级管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    @JvmStatic
    fun isSuperAdmin(userId: Long?): Boolean {
        return SystemConstants.SUPER_ADMIN_ID == userId
    }

    /**
     * 是否为超级管理员
     *
     * @return 结果
     */
    @JvmStatic
    fun isSuperAdmin(): Boolean {
        return isSuperAdmin(getUserId())
    }

    /**
     * 是否为租户管理员
     *
     * @param rolePermission 角色权限标识组
     * @return 结果
     */
    @JvmStatic
    fun isTenantAdmin(rolePermission: Set<String>?): Boolean {
        if (rolePermission.isNullOrEmpty()) {
            return false
        }
        return rolePermission.contains(TenantConstants.TENANT_ADMIN_ROLE_KEY)
    }

    /**
     * 是否为租户管理员
     *
     * @return 结果
     */
    @JvmStatic
    fun isTenantAdmin(): Boolean {
        val loginUser = getLoginUser() ?: return false
        return Convert.toBool(isTenantAdmin(loginUser.rolePermission))
    }

    /**
     * 检查当前用户是否已登录
     *
     * @return 结果
     */
    @JvmStatic
    fun isLogin(): Boolean {
        return try {
            StpUtil.checkLogin()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 登出
     */
    @JvmStatic
    fun logout() {
        StpUtil.logout()
    }
}
