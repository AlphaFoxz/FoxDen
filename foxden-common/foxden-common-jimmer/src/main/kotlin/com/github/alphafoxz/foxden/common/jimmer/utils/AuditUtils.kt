package com.github.alphafoxz.foxden.common.jimmer.utils

import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import java.time.LocalDateTime

/**
 * 审计字段自动填充工具类
 *
 * 提供类似 MyBatis-Plus MetaObjectHandler 的功能，
 * 在保存和更新实体时自动填充 createBy, updateBy, createTime, updateTime 等审计字段。
 *
 * @see org.dromara.common.mybatis.handler.InjectionMetaObjectHandler
 */
object AuditUtils {

    /**
     * 如果用户不存在，默认注入 -1 代表无用户
     */
    const val DEFAULT_USER_ID = -1L

    /**
     * 获取当前登录用户ID
     * 如果未登录，返回默认用户ID (-1)
     */
    @JvmStatic
    fun getCurrentUserId(): Long {
        return try {
            val loginUser = LoginHelper.getLoginUser()
            loginUser?.userId ?: DEFAULT_USER_ID
        } catch (e: Exception) {
            DEFAULT_USER_ID
        }
    }

    /**
     * 获取当前时间
     */
    @JvmStatic
    fun getCurrentTime(): LocalDateTime {
        return LocalDateTime.now()
    }

    /**
     * 获取创建审计字段值
     *
     * @return Pair<用户ID, 当前时间>
     */
    @JvmStatic
    fun getCreateAuditInfo(): Pair<Long, LocalDateTime> {
        return Pair(getCurrentUserId(), getCurrentTime())
    }

    /**
     * 获取更新审计字段值
     *
     * @return Pair<用户ID, 当前时间>
     */
    @JvmStatic
    fun getUpdateAuditInfo(): Pair<Long, LocalDateTime> {
        return Pair(getCurrentUserId(), getCurrentTime())
    }
}
