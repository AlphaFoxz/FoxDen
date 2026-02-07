package com.github.alphafoxz.foxden.common.sms.handler

import cn.hutool.http.HttpStatus
import com.github.alphafoxz.foxden.common.core.domain.R
import org.dromara.sms4j.comm.exception.SmsBlendException
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import jakarta.servlet.http.HttpServletRequest

/**
 * SMS异常处理器
 *
 * @author AprilWind
 */
@RestControllerAdvice
class SmsExceptionHandler {
    private val log = LoggerFactory.getLogger(SmsExceptionHandler::class.java)

    /**
     * sms异常
     */
    @ExceptionHandler(SmsBlendException::class)
    fun handleSmsBlendException(e: SmsBlendException, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        log.error("请求地址'{}',发生sms短信异常.", requestURI, e)
        return R.fail(HttpStatus.HTTP_INTERNAL_ERROR, "短信发送失败，请稍后再试...")
    }
}
