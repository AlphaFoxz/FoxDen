package com.github.alphafoxz.foxden.common.web.filter

import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import java.io.IOException

/**
 * Repeatable 过滤器
 */
class RepeatableFilter : Filter {
    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig) {
        // Do nothing
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        var requestWrapper: ServletRequest? = null
        if (request is HttpServletRequest &&
            StringUtils.startsWithIgnoreCase(request.contentType, MediaType.APPLICATION_JSON_VALUE)
        ) {
            requestWrapper = RepeatedlyRequestWrapper(request, response)
        }
        if (requestWrapper == null) {
            chain.doFilter(request, response)
        } else {
            chain.doFilter(requestWrapper, response)
        }
    }

    override fun destroy() {
        // Do nothing
    }
}
