package com.github.alphafoxz.foxden.common.web.handler

import cn.hutool.core.util.ObjectUtil
import cn.hutool.http.HttpStatus
import com.fasterxml.jackson.core.JsonParseException
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.exception.BaseException
import com.github.alphafoxz.foxden.common.core.utils.StreamUtils
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import java.io.IOException

/**
 * 全局异常处理器
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupported(
        e: HttpRequestMethodNotSupportedException,
        request: HttpServletRequest
    ): R<Void> {
        val requestURI = request.requestURI
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.method)
        val msg = e.message ?: "请求方式不支持"
        return R.fail(HttpStatus.HTTP_BAD_METHOD.toInt(), msg)
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException::class)
    fun handleServiceException(e: ServiceException, request: HttpServletRequest): R<Void> {
        log.error(e.message)
        val code = e.code
        val msg = e.message ?: "业务异常"
        return if (ObjectUtil.isNotNull(code)) R.fail(code!!, msg) else R.fail(msg)
    }

    /**
     * Servlet 异常
     */
    @ExceptionHandler(ServletException::class)
    fun handleServletException(e: ServletException, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        log.error("请求地址'{}',发生未知异常.", requestURI, e)
        return R.fail(e.message ?: "Servlet异常")
    }

    /**
     * 基础异常
     */
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException, request: HttpServletRequest): R<Void> {
        log.error(e.message)
        return R.fail(e.message ?: "基础异常")
    }

    /**
     * 请求路径中缺少必需的路径变量
     */
    @ExceptionHandler(MissingPathVariableException::class)
    fun handleMissingPathVariableException(
        e: MissingPathVariableException,
        request: HttpServletRequest
    ): R<Void> {
        val requestURI = request.requestURI
        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", requestURI)
        return R.fail(String.format("请求路径中缺少必需的路径变量[%s]", e.variableName))
    }

    /**
     * 请求参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(
        e: MethodArgumentTypeMismatchException,
        request: HttpServletRequest
    ): R<Void> {
        val requestURI = request.requestURI
        log.error("请求参数类型不匹配'{}',发生系统异常.", requestURI)
        return R.fail(
            String.format(
                "请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'",
                e.name,
                e.requiredType?.name,
                e.value
            )
        )
    }

    /**
     * 找不到路由
     */
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(e: NoHandlerFoundException, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        log.error("请求地址'{}'不存在.", requestURI)
        return R.fail(HttpStatus.HTTP_NOT_FOUND.toInt(), e.message ?: "请求地址不存在")
    }

    /**
     * 拦截未知的运行时异常
     */
    @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IOException::class)
    fun handleRuntimeException(e: IOException, request: HttpServletRequest) {
        val requestURI = request.requestURI
        if (requestURI.contains("sse")) {
            // sse 经常性连接中断 例如关闭浏览器 直接屏蔽
            return
        }
        log.error("请求地址'{}',连接中断", requestURI, e)
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(e: RuntimeException, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        log.error("请求地址'{}',发生未知异常.", requestURI, e)
        return R.fail(e.message ?: "运行时异常")
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        log.error("请求地址'{}',发生系统异常.", requestURI, e)
        return R.fail(e.message ?: "系统异常")
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException::class)
    fun handleBindException(e: BindException): R<Void> {
        log.error(e.message)
        val message = StreamUtils.join(e.allErrors, { obj -> obj.defaultMessage }, ", ")
        return R.fail(message)
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun constraintViolationException(e: ConstraintViolationException): R<Void> {
        log.error(e.message)
        val message = StreamUtils.join(e.constraintViolations, { obj -> obj.message }, ", ")
        return R.fail(message)
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): R<Void> {
        log.error(e.message)
        val message = StreamUtils.join(e.bindingResult.allErrors, { obj -> obj.defaultMessage }, ", ")
        return R.fail(message)
    }

    /**
     * JSON 解析异常
     */
    @ExceptionHandler(JsonParseException::class)
    fun handleJsonParseException(e: JsonParseException, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        log.error("请求地址'{}' 发生 JSON 解析异常: {}", requestURI, e.message)
        val originalMsg = e.message ?: ""
        return R.fail(HttpStatus.HTTP_BAD_REQUEST.toInt(), "请求数据格式错误（JSON 解析失败）：$originalMsg")
    }

    /**
     * 请求体读取异常
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException, request: HttpServletRequest): R<Void> {
        log.error("请求地址'{}', 参数解析失败: {}", request.requestURI, e.message)
        val causeMsg = e.mostSpecificCause?.message ?: ""
        return R.fail(HttpStatus.HTTP_BAD_REQUEST.toInt(), "请求参数格式错误：$causeMsg")
    }
}
