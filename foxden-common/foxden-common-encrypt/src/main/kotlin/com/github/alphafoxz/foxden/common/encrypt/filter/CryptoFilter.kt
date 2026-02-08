package com.github.alphafoxz.foxden.common.encrypt.filter

import com.github.alphafoxz.foxden.common.core.constant.HttpStatus
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.encrypt.annotation.ApiEncrypt
import com.github.alphafoxz.foxden.common.encrypt.properties.ApiDecryptProperties
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.HandlerExecutionChain
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

/**
 * Crypto 过滤器
 *
 * 自动处理请求体的解密和响应体的加密
 * 配合 @ApiEncrypt 注解使用
 *
 * @author wdhcr
 */
class CryptoFilter(
    private val properties: ApiDecryptProperties
) : Filter {

    private val log = LoggerFactory.getLogger(CryptoFilter::class.java)

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val servletRequest = request as HttpServletRequest
        val servletResponse = response as HttpServletResponse

        // 获取加密注解
        val apiEncrypt = getApiEncryptAnnotation(servletRequest)
        val responseFlag = apiEncrypt != null && apiEncrypt.response

        var requestWrapper: ServletRequest? = null
        var responseWrapper: ServletResponse? = null
        var responseBodyWrapper: EncryptResponseBodyWrapper? = null

        // 是否为 put 或者 post 请求
        if (HttpMethod.PUT.matches(servletRequest.method) || HttpMethod.POST.matches(servletRequest.method)) {
            // 是否存在加密标头
            val headerValue = servletRequest.getHeader(properties.headerFlag)
            if (StringUtils.isNotBlank(headerValue)) {
                // 请求解密
                try {
                    requestWrapper = DecryptRequestBodyWrapper(
                        servletRequest,
                        properties.privateKey!!,
                        properties.headerFlag!!
                    )
                    log.debug("请求解密成功")
                } catch (e: Exception) {
                    log.error("请求解密失败", e)
                    // 解密失败，返回错误
                    val exceptionResolver = SpringUtils.getBean(
                        "handlerExceptionResolver",
                        HandlerExceptionResolver::class.java
                    )
                    exceptionResolver.resolveException(
                        servletRequest,
                        servletResponse,
                        null,
                        ServiceException("请求解密失败，请检查加密格式", HttpStatus.BAD_REQUEST)
                    )
                    return
                }
            } else {
                // 没有加密头，放行（可能是明文请求）
                log.debug("请求无加密标识，按明文处理")
            }
        }

        // 判断是否响应加密
        if (responseFlag) {
            responseBodyWrapper = EncryptResponseBodyWrapper(servletResponse)
            responseWrapper = responseBodyWrapper
        }

        chain.doFilter(
            requestWrapper ?: request,
            responseWrapper ?: response
        )

        if (responseFlag) {
            servletResponse.reset()
            // 对原始内容加密
            val encryptContent = responseBodyWrapper!!.getEncryptContent(
                servletResponse,
                properties.publicKey!!,
                properties.headerFlag!!
            )
            // 对加密后的内容写出
            servletResponse.writer.write(encryptContent)
        }
    }

    /**
     * 获取 ApiEncrypt 注解
     */
    private fun getApiEncryptAnnotation(servletRequest: HttpServletRequest): ApiEncrypt? {
        val handlerMapping = SpringUtils.getBean(
            "requestMappingHandlerMapping",
            RequestMappingHandlerMapping::class.java
        )
        // 获取注解
        try {
            val mappingHandler = handlerMapping.getHandler(servletRequest)
            if (mappingHandler != null) {
                val handler = mappingHandler.handler
                if (handler != null && handler is HandlerMethod) {
                    // 从handler获取注解
                    return handler.getMethodAnnotation(ApiEncrypt::class.java)
                }
            }
        } catch (e: Exception) {
            log.error("获取 ApiEncrypt 注解失败", e)
        }
        return null
    }

    override fun init(filterConfig: FilterConfig) {
        log.info("CryptoFilter 初始化成功")
    }

    override fun destroy() {
        // 清理资源
    }
}
