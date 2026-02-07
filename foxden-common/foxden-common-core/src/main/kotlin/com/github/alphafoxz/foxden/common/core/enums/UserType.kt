package com.github.alphafoxz.foxden.common.core.enums

import com.github.alphafoxz.foxden.common.core.utils.StringUtils

/**
 * 用户类型
 */
enum class UserType(val userType: String) {
    /** 后台系统用户 */
    SYS_USER("sys_user"),

    /** 移动客户端用户 */
    APP_USER("app_user");

    companion object {
        /**
         * 根据字符串获取用户类型
         */
        @JvmStatic
        fun getUserType(str: String?): UserType {
            return values().firstOrNull { str?.contains(it.userType) == true }
                ?: throw RuntimeException("'UserType' not found By $str")
        }
    }
}
