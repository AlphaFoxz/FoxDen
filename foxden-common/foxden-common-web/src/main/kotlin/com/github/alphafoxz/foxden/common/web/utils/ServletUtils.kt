package com.github.alphafoxz.foxden.common.web.utils

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * Servlet工具类
 */
object ServletUtils {

    /**
     * 获取request
     */
    @JvmStatic
    fun getRequest(): HttpServletRequest {
        val requestAttributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
        return requestAttributes?.request ?: throw IllegalStateException("No current HttpServletRequest")
    }

    /**
     * 获取客户端IP地址
     */
    @JvmStatic
    fun getClientIP(): String {
        val request = getRequest()
        var ip = request.getHeader("X-Forwarded-For")
        if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("Proxy-Client-IP")
        }
        if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_CLIENT_IP")
        }
        if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR")
        }
        if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.remoteAddr
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        val commaIndex = ip.indexOf(',')
        if (commaIndex != -1) {
            ip = ip.substring(0, commaIndex)
        }
        return ip.trim()
    }

    /**
     * 获取请求参数Map
     */
    @JvmStatic
    fun getParamMap(request: HttpServletRequest): Map<String, Array<String>> {
        return request.parameterMap
    }

    /**
     * HttpServletRequest的扩展属性 - 获取客户端IP
     */
    val HttpServletRequest.ip: String
        get() = getClientIP()

    /**
     * HttpServletRequest的扩展属性 - 获取租户ID
     */
    val HttpServletRequest.tenant: String
        get() = getHeader("tenant-id") ?: getParameter("tenant-id") ?: "000000"
}
