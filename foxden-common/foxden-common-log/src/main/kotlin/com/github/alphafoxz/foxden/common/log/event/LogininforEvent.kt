package com.github.alphafoxz.foxden.common.log.event

import jakarta.servlet.http.HttpServletRequest
import java.io.Serializable

/**
 * 登录事件
 */
data class LogininforEvent(
    /**
     * 租户ID
     */
    var tenantId: String? = null,

    /**
     * 用户账号
     */
    var username: String? = null,

    /**
     * 登录状态 0成功 1失败
     */
    var status: String? = null,

    /**
     * 提示消息
     */
    var message: String? = null,

    /**
     * 请求体
     */
    var request: HttpServletRequest? = null,

    /**
     * 其他参数
     */
    var args: Array<Any>? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
