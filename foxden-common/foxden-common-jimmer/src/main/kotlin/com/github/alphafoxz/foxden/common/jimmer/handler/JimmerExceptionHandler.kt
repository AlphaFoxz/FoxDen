package com.github.alphafoxz.foxden.common.jimmer.handler

import cn.hutool.http.HttpStatus
import com.github.alphafoxz.foxden.common.core.domain.R
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Jimmer异常处理器
 */
@RestControllerAdvice
class JimmerExceptionHandler {
    private val log = LoggerFactory.getLogger(JimmerExceptionHandler::class.java)

    /**
     * 主键或UNIQUE索引，数据重复异常
     */
    @ExceptionHandler(DuplicateKeyException::class)
    fun handleDuplicateKeyException(e: DuplicateKeyException, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        log.error("请求地址'{}',数据库中已存在记录'{}'", requestURI, e.message)
        return R.fail(HttpStatus.HTTP_CONFLICT, "数据库中已存在该记录，请联系管理员确认")
    }

    /**
     * 数据库通用异常处理
     */
    @ExceptionHandler(Exception::class)
    fun handleDatabaseException(e: Exception, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        val message = e.message
        log.error("请求地址'{}', 数据库异常", requestURI, e)
        return R.fail(HttpStatus.HTTP_INTERNAL_ERROR, message ?: "数据库操作失败")
    }
}
