package com.github.alphafoxz.foxden.common.web.filter

import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.web.config.properties.XssProperties
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import java.io.IOException

/**
 * 防止XSS攻击的过滤器
 */
class XssFilter : Filter {
    /**
     * 排除链接
     */
    val excludes = mutableListOf<String>()

    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig) {
        val properties = SpringUtils.getBean(XssProperties::class.java)
        excludes.addAll(properties.excludeUrls)
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val req = request as HttpServletRequest
        val resp = response as HttpServletResponse
        if (handleExcludeURL(req, resp)) {
            chain.doFilter(request, response)
            return
        }
        val xssRequest = XssHttpServletRequestWrapper(req)
        chain.doFilter(xssRequest, response)
    }

    private fun handleExcludeURL(request: HttpServletRequest, response: HttpServletResponse): Boolean {
        val url = request.servletPath
        val method = request.method
        // GET DELETE 不过滤
        if (method == null || HttpMethod.GET.matches(method) || HttpMethod.DELETE.matches(method)) {
            return true
        }
        return StringUtils.matches(url, excludes)
    }

    override fun destroy() {
        // Do nothing
    }
}
