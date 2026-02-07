package com.github.alphafoxz.foxden.common.web.filter

import cn.hutool.core.io.IoUtil
import cn.hutool.core.map.MapUtil
import cn.hutool.core.util.ArrayUtil
import cn.hutool.http.HtmlUtil
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.HashMap

/**
 * XSS 过滤处理
 */
class XssHttpServletRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    override fun getParameter(name: String?): String? {
        val value = super.getParameter(name) ?: return null
        return HtmlUtil.cleanHtmlTag(value).trim()
    }

    override fun getParameterMap(): Map<String, Array<String>> {
        val valueMap = super.getParameterMap()
        if (MapUtil.isEmpty(valueMap)) {
            return valueMap
        }
        // 避免某些容器不允许改参数的情况 copy一份重新改
        val map = HashMap(valueMap)
        for (entry in map.entries) {
            val values = entry.value
            if (values != null) {
                val length = values.size
                val escapeValues = arrayOfNulls<String>(length)
                for (i in 0 until length) {
                    // 防xss攻击和过滤前后空格
                    escapeValues[i] = HtmlUtil.cleanHtmlTag(values[i]).trim()
                }
                @Suppress("UNCHECKED_CAST")
                map[entry.key] = escapeValues as Array<String>
            }
        }
        return map
    }

    override fun getParameterValues(name: String?): Array<String>? {
        val values = super.getParameterValues(name) ?: return null
        if (ArrayUtil.isEmpty(values)) {
            return values
        }
        val length = values.size
        val escapeValues = arrayOfNulls<String>(length)
        for (i in 0 until length) {
            // 防xss攻击和过滤前后空格
            escapeValues[i] = HtmlUtil.cleanHtmlTag(values[i]).trim()
        }
        @Suppress("UNCHECKED_CAST")
        return escapeValues as Array<String>
    }

    @Throws(IOException::class)
    override fun getInputStream(): ServletInputStream {
        // 非json类型，直接返回
        if (!isJsonRequest) {
            return super.getInputStream()
        }

        // 为空，直接返回
        val json = IoUtil.readBytes(super.getInputStream(), false).toString(StandardCharsets.UTF_8)
        if (StringUtils.isEmpty(json)) {
            return super.getInputStream()
        }

        // xss过滤
        val cleanedJson = HtmlUtil.cleanHtmlTag(json).trim()
        val jsonBytes = cleanedJson.toByteArray(StandardCharsets.UTF_8)
        return SimpleServletInputStream(jsonBytes)
    }

    /**
     * 是否是Json请求
     */
    private val isJsonRequest: Boolean
        get() {
            val header = super.getHeader(HttpHeaders.CONTENT_TYPE)
            return StringUtils.startsWithIgnoreCase(header, MediaType.APPLICATION_JSON_VALUE)
        }
}
