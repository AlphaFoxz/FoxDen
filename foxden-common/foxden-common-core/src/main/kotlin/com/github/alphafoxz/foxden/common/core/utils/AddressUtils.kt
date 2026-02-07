package com.github.alphafoxz.foxden.common.core.utils

import cn.hutool.core.util.StrUtil
import jakarta.servlet.http.HttpServletRequest

/**
 * 地址工具类
 *
 * @author FoxDen Team
 */
object AddressUtils {

    /**
     * 获取客户端IP地址
     *
     * @param request 请求对象
     * @return IP地址
     */
    @JvmStatic
    fun getIpAddr(request: HttpServletRequest): String {
        if (request == null) {
            return "unknown"
        }
        var ip = request.getHeader("x-forwarded-for")
        if (isUnknown(ip)) {
            ip = request.getHeader("Proxy-Client-IP")
        }
        if (isUnknown(ip)) {
            ip = request.getHeader("X-Forwarded-For")
        }
        if (isUnknown(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }
        if (isUnknown(ip)) {
            ip = request.getHeader("X-Real-IP")
        }
        if (isUnknown(ip)) {
            ip = request.remoteAddr
        }
        // IPv6 本地地址转换为 IPv4
        return if ("0:0:0:0:0:0:0:1" == ip || "::1" == ip) {
            "127.0.0.1"
        } else {
            ip
        }
    }

    /**
     * 判断是否为未知IP
     *
     * @param ip IP地址
     * @return 是否为未知
     */
    @JvmStatic
    fun isUnknown(ip: String?): Boolean {
        return StrUtil.isBlank(ip) || "unknown".equals(ip, ignoreCase = true)
    }

    /**
     * 根据IP地址获取真实地址
     * 这是一个简化版本，实际项目中可以集成 IP 地址库
     *
     * @param ip IP地址
     * @return 真实地址
     */
    @JvmStatic
    fun getRealAddressByIP(ip: String?): String {
        if (StrUtil.isBlank(ip)) {
            return "未知地址"
        }
        // 简化版本：返回内网IP或公网IP
        return if (internalIp(ip!!)) {
            "内网IP"
        } else {
            "公网IP"
        }
    }

    /**
     * 是否为内网IP
     *
     * @param ip IP地址
     * @return 是否为内网IP
     */
    @JvmStatic
    fun internalIp(ip: String): Boolean {
        if (StrUtil.isBlank(ip) || "localhost".equals(ip, ignoreCase = true)) {
            return true
        }
        val bytes = toBytes(ip) ?: return true
        // 10.0.0.0 - 10.255.255.255
        if (bytes[0] == 10.toByte()) {
            return true
        }
        // 172.16.0.0 - 172.31.255.255
        if (bytes[0] == 172.toByte() && bytes[1] >= 16.toByte() && bytes[1] <= 31.toByte()) {
            return true
        }
        // 192.168.0.0 - 192.168.255.255
        if (bytes[0] == 192.toByte() && bytes[1] == 168.toByte()) {
            return true
        }
        // 127.0.0.0 - 127.255.255.255
        if (bytes[0] == 127.toByte()) {
            return true
        }
        return false
    }

    /**
     * 将IP地址转换为字节数组
     *
     * @param ip IP地址
     * @return 字节数组
     */
    @JvmStatic
    fun toBytes(ip: String): ByteArray? {
        if (StrUtil.isBlank(ip)) {
            return null
        }
        val parts = ip.split("\\.".toRegex())
        if (parts.size != 4) {
            return null
        }
        return try {
            ByteArray(4) { i ->
                parts[i].toInt().toByte()
            }
        } catch (e: NumberFormatException) {
            null
        }
    }
}
