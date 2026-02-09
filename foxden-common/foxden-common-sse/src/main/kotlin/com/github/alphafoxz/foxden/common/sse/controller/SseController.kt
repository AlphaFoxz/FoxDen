package com.github.alphafoxz.foxden.common.sse.controller

import cn.dev33.satoken.annotation.SaIgnore
import cn.dev33.satoken.stp.StpUtil
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.common.sse.core.SseEmitterManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

/**
 * SSE 控制器
 *
 * @author FoxDen Team
 */
@RestController
@ConditionalOnProperty(value = ["sse.enabled"], havingValue = "true")
class SseController(
    private val sseEmitterManager: SseEmitterManager
) {

    /**
     * 建立 SSE 连接
     */
    @GetMapping(value = ["\${sse.path}"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun connect(): SseEmitter? {
        if (!StpUtil.isLogin()) {
            return null
        }
        val tokenValue = StpUtil.getTokenValue()
        val userId = LoginHelper.getUserId()
        return sseEmitterManager.connect(userId!!, tokenValue)
    }

    /**
     * 关闭 SSE 连接
     */
    @SaIgnore
    @GetMapping(value = ["\${sse.path}/close"])
    fun close(): R<Void> {
        val tokenValue = StpUtil.getTokenValue()
        val userId = LoginHelper.getUserId()
        sseEmitterManager.disconnect(userId, tokenValue)
        return R.ok()
    }
}
