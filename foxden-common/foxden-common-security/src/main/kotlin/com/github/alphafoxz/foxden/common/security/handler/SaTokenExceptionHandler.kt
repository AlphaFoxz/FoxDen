package com.github.alphafoxz.foxden.common.security.handler

import cn.dev33.satoken.exception.NotLoginException
import cn.dev33.satoken.exception.NotPermissionException
import cn.dev33.satoken.exception.NotRoleException
import cn.hutool.http.HttpStatus
import com.github.alphafoxz.foxden.common.core.domain.R
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Sa-Token 异常处理器
 * 设置最高优先级，确保在 GlobalExceptionHandler 之前处理 Sa-Token 相关异常
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class SaTokenExceptionHandler {
    private val log = LoggerFactory.getLogger(SaTokenExceptionHandler::class.java)

    /**
     * 权限码异常
     */
    @ExceptionHandler(NotPermissionException::class)
    fun handleNotPermissionException(e: NotPermissionException, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        log.error("请求地址'{}',权限码校验失败'{}'", requestURI, e.message)
        return R.fail(HttpStatus.HTTP_FORBIDDEN, "没有访问权限，请联系管理员授权")
    }

    /**
     * 角色权限异常
     */
    @ExceptionHandler(NotRoleException::class)
    fun handleNotRoleException(e: NotRoleException, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        log.error("请求地址'{}',角色权限校验失败'{}'", requestURI, e.message)
        return R.fail(HttpStatus.HTTP_FORBIDDEN, "没有访问权限，请联系管理员授权")
    }

    /**
     * 认证失败
     */
    @ExceptionHandler(NotLoginException::class)
    fun handleNotLoginException(e: NotLoginException, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        log.error("请求地址'{}',认证失败'{}',无法访问系统资源", requestURI, e.message)
        return R.fail(HttpStatus.HTTP_UNAUTHORIZED, "认证失败，无法访问系统资源")
    }
}
