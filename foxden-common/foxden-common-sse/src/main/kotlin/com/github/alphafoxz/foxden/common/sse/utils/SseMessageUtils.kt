package com.github.alphafoxz.foxden.common.sse.utils

import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.sse.core.SseEmitterManager
import com.github.alphafoxz.foxden.common.sse.dto.SseMessageDto

/**
 * SSE 工具类
 *
 * @author FoxDen Team
 */
object SseMessageUtils {

    private val SSE_ENABLE: Boolean = SpringUtils.getProperty("sse.enabled", "true").toBoolean()
    private val MANAGER: SseEmitterManager? by lazy {
        if (isEnable()) {
            SpringUtils.getBean(SseEmitterManager::class.java)
        } else {
            null
        }
    }

    /**
     * 向指定的 SSE 会话发送消息
     *
     * @param userId 要发送消息的用户id
     * @param message 要发送的消息内容
     */
    @JvmStatic
    fun sendMessage(userId: Long, message: String) {
        if (!isEnable()) {
            return
        }
        MANAGER?.sendMessage(userId, message)
    }

    /**
     * 本机全用户会话发送消息
     *
     * @param message 要发送的消息内容
     */
    @JvmStatic
    fun sendMessage(message: String) {
        if (!isEnable()) {
            return
        }
        MANAGER?.sendMessage(message)
    }

    /**
     * 发布 SSE 订阅消息
     *
     * @param sseMessageDto 要发布的 SSE 消息对象
     */
    @JvmStatic
    fun publishMessage(sseMessageDto: SseMessageDto) {
        if (!isEnable()) {
            return
        }
        MANAGER?.publishMessage(sseMessageDto)
    }

    /**
     * 向所有的用户发布订阅的消息(群发)
     *
     * @param message 要发布的消息内容
     */
    @JvmStatic
    fun publishAll(message: String) {
        if (!isEnable()) {
            return
        }
        MANAGER?.publishAll(message)
    }

    /**
     * 是否开启
     */
    @JvmStatic
    fun isEnable(): Boolean {
        return SSE_ENABLE
    }
}
