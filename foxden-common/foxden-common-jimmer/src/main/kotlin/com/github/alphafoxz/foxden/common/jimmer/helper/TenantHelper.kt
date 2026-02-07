package com.github.alphafoxz.foxden.common.jimmer.helper

import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.core.constant.TenantConstants
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory

/**
 * 租户助手类
 *
 * @author FoxDen Team
 */
object TenantHelper {

    private val log = LoggerFactory.getLogger(TenantHelper::class.java)

    /**
     * 获取租户ID
     *
     * @return 租户ID
     */
    @JvmStatic
    fun getTenantId(): String {
        return getTenantId(SpringUtils.getBean(HttpServletRequest::class.java))
    }

    /**
     * 获取租户ID
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
     * 动态切换租户
     *
     * @param tenantId 租户ID
     * @param runnable 执行的代码
     */
    @JvmStatic
    fun dynamicTenant(tenantId: String, runnable: Runnable) {
        val oldTenantId = getTenantId()
        try {
            // TODO: 实现租户上下文设置
            // 这里可以通过ThreadLocal或其他方式存储租户上下文
            runnable.run()
        } finally {
            // 恢复原租户
            // TODO: 实现租户上下文恢复
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
    fun <T> dynamicTenant(tenantId: String, supplier: java.util.function.Supplier<T>): T {
        val oldTenantId = getTenantId()
        try {
            // TODO: 实现租户上下文设置
            return supplier.get()
        } finally {
            // 恢复原租户
            // TODO: 实现租户上下文恢复
        }
    }

    /**
     * 动态切换租户（使用函数）
     *
     * @param tenantId 租户ID
     * @param block 执行的代码块
     * @return 执行结果
     */
    inline fun <T> dynamic(tenantId: String, block: () -> T): T {
        val oldTenantId = getTenantId()
        try {
            // TODO: 实现租户上下文设置
            return block()
        } finally {
            // 恢复原租户
            // TODO: 实现租户上下文恢复
        }
    }

    /**
     * 是否为系统管理员
     *
     * @return 是否为系统管理员
     */
    @JvmStatic
    fun isSystemAdmin(): Boolean {
        // TODO: 实现系统管理员判断
        return false
    }

    /**
     * 是否为超级管理员
     *
     * @return 是否为超级管理员
     */
    @JvmStatic
    fun isSuperAdmin(): Boolean {
        // TODO: 实现超级管理员判断
        return false
    }

    /**
     * 是否启用租户功能
     *
     * @return 是否启用
     */
    @JvmStatic
    fun isEnable(): Boolean {
        // TODO: 从配置中读取租户开关，暂时返回true
        return true
    }

    /**
     * 动态租户 - 暂时返回默认值
     */
    @JvmStatic
    fun dynamicTenant(block: () -> Unit) {
        // TODO: 实现动态租户切换
        block()
    }

    /**
     * 清除动态租户
     */
    @JvmStatic
    fun clearDynamic() {
        // TODO: 实现清除动态租户
    }
}
