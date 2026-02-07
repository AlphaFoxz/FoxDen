package com.github.alphafoxz.foxden.common.redis.handler

import cn.hutool.http.HttpStatus
import com.baomidou.lock.exception.LockFailureException
import com.github.alphafoxz.foxden.common.core.domain.R
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Redis异常处理器
 */
@RestControllerAdvice
class RedisExceptionHandler {
    private val log = LoggerFactory.getLogger(RedisExceptionHandler::class.java)

    /**
     * 分布式锁Lock4j异常
     */
    @ExceptionHandler(LockFailureException::class)
    fun handleLockFailureException(e: LockFailureException, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        log.error("获取锁失败了'{}',发生Lock4j异常.", requestURI, e)
        return R.fail(HttpStatus.HTTP_UNAVAILABLE, "业务处理中，请稍后再试...")
    }
}
