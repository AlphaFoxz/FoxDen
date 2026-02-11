package com.github.alphafoxz.foxden.common.jimmer.helper

import com.github.alphafoxz.foxden.common.core.constant.TenantConstants
import com.github.alphafoxz.foxden.common.core.config.TenantProperties
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.common.web.utils.ServletUtils
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import java.util.function.Supplier

/**
 * 租户助手类
 *
 * @author FoxDen Team
 */
object TenantHelper {

    private val log = LoggerFactory.getLogger(TenantHelper::class.java)

    /** 动态租户上下文（线程本地） */
    private val DYNAMIC_TENANT_CONTEXT = ThreadLocal<String>()

    /**
     * 获取租户ID（优先使用动态租户）
     *
     * @return 租户ID
     */
    @JvmStatic
    fun getTenantId(): String {
        // 优先获取动态设置的租户
        val dynamicTenant = DYNAMIC_TENANT_CONTEXT.get()
        if (StringUtils.isNotBlank(dynamicTenant)) {
            return dynamicTenant
        }
        // 从登录信息获取租户
        return LoginHelper.getTenantId() ?: TenantConstants.DEFAULT_TENANT_ID
    }

    /**
     * 获取租户ID（从请求中）
     *
     * @param request 请求对象
     * @return 租户ID
     */
    @JvmStatic
    fun getTenantId(request: HttpServletRequest): String {
        var tenantId = request.getHeader(TenantConstants.TENANT_ID)
        if (StringUtils.isEmpty(tenantId)) {
            tenantId = request.getParameter(TenantConstants.TENANT_ID)
        }
        return tenantId ?: TenantConstants.DEFAULT_TENANT_ID
    }

    /**
     * 设置动态租户
     *
     * @param tenantId 租户ID
     * @param persist 是否持久化到Session
     */
    @JvmStatic
    fun setDynamic(tenantId: String, persist: Boolean = false) {
        DYNAMIC_TENANT_CONTEXT.set(tenantId)
    }

    /**
     * 动态切换租户
     *
     * @param tenantId 租户ID
     * @param runnable 执行的代码
     */
    @JvmStatic
    fun dynamicTenant(tenantId: String, runnable: Runnable) {
        setDynamic(tenantId)
        try {
            runnable.run()
        } finally {
            clearDynamic()
        }
    }

    /**
     * 动态切换租户（带返回值）
     *
     * @param tenantId 租户ID
     * @param supplier 执行的代码
     * @return 执行结果
     */
    @JvmStatic
    fun <T> dynamicTenant(tenantId: String, supplier: Supplier<T>): T {
        setDynamic(tenantId)
        try {
            return supplier.get()
        } finally {
            clearDynamic()
        }
    }

    /**
     * 清除动态租户
     */
    @JvmStatic
    fun clearDynamic() {
        DYNAMIC_TENANT_CONTEXT.remove()
    }

    /**
     * 是否为系统管理员
     * 系统管理员指 userId == 1 的用户
     *
     * @return 是否为系统管理员
     */
    @JvmStatic
    fun isSystemAdmin(): Boolean {
        val userId = LoginHelper.getUserId() ?: return false
        return TenantConstants.SUPER_ADMIN_ID == userId
    }

    /**
     * 是否为超级管理员
     * 超级管理员拥有 superadmin 角色
     *
     * @return 是否为超级管理员
     */
    @JvmStatic
    fun isSuperAdmin(): Boolean {
        return LoginHelper.isSuperAdmin()
    }

    /**
     * 是否启用租户功能
     *
     * @return 是否启用
     */
    @JvmStatic
    fun isEnable(): Boolean {
        // 从配置读取租户开关
        return SpringUtils.getBean(TenantProperties::class.java)?.enable ?: true
    }

    /**
     * 获取租户配置
     *
     * @return 租户配置
     */
    @JvmStatic
    fun getTenantProperties(): TenantProperties? {
        return SpringUtils.getBean(TenantProperties::class.java)
    }

    /**
     * 忽略租户过滤执行代码
     * 用于需要跨租户操作的场景
     *
     * @param handle 执行的代码
     */
    @JvmStatic
    fun ignore(handle: Runnable) {
        // 清除动态租户上下文，暂时恢复到默认租户
        val oldTenant = DYNAMIC_TENANT_CONTEXT.get()
        try {
            DYNAMIC_TENANT_CONTEXT.remove()
            handle.run()
        } finally {
            if (StringUtils.isNotBlank(oldTenant)) {
                DYNAMIC_TENANT_CONTEXT.set(oldTenant)
            }
        }
    }

    /**
     * 忽略租户过滤执行代码（带返回值）
     * 用于需要跨租户操作的场景
     *
     * @param handle 执行的代码
     * @return 执行结果
     */
    @JvmStatic
    fun <T> ignore(handle: Supplier<T>): T {
        // 清除动态租户上下文，暂时恢复到默认租户
        val oldTenant = DYNAMIC_TENANT_CONTEXT.get()
        try {
            DYNAMIC_TENANT_CONTEXT.remove()
            return handle.get()
        } finally {
            if (StringUtils.isNotBlank(oldTenant)) {
                DYNAMIC_TENANT_CONTEXT.set(oldTenant)
            }
        }
    }

    /**
     * 忽略租户过滤执行代码（Kotlin 函数）
     * 用于需要跨租户操作的场景
     *
     * @param block 执行的代码块
     * @return 执行结果
     */
    @JvmStatic
    fun <T> ignore(block: () -> T): T {
        val oldTenant = DYNAMIC_TENANT_CONTEXT.get()
        try {
            DYNAMIC_TENANT_CONTEXT.remove()
            return block()
        } finally {
            if (StringUtils.isNotBlank(oldTenant)) {
                DYNAMIC_TENANT_CONTEXT.set(oldTenant)
            }
        }
    }

    /**
     * 动态切换租户（Kotlin 函数）
     *
     * @param tenantId 租户ID
     * @param block 执行的代码块
     * @return 执行结果
     */
    @JvmStatic
    fun <T> dynamicTenant(tenantId: String, block: () -> T): T {
        setDynamic(tenantId)
        try {
            return block()
        } finally {
            clearDynamic()
        }
    }

    /**
     * 校验是否为默认租户
     *
     * @param tenantId 租户ID
     * @return 是否为默认租户
     */
    @JvmStatic
    fun isDefaultTenant(tenantId: String?): Boolean {
        return TenantConstants.DEFAULT_TENANT_ID == tenantId
    }
}
