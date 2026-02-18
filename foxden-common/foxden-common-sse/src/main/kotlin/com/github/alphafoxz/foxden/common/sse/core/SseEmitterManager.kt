package com.github.alphafoxz.foxden.common.sse.core

import cn.hutool.core.map.MapUtil
import cn.hutool.json.JSONUtil
import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import com.github.alphafoxz.foxden.common.sse.dto.SseMessageDto
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

/**
 * SSE 连接管理器
 *
 * 管理 Server-Sent Events (SSE) 连接
 *
 * @author FoxDen Team
 */
class SseEmitterManager {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        /**
         * 订阅的频道
         */
        private const val SSE_TOPIC = "global:sse"

        /**
         * 用户Token映射表：userId -> (token -> SseEmitter)
         */
        private val USER_TOKEN_EMITTERS = ConcurrentHashMap<Long, ConcurrentHashMap<String, SseEmitter>>()
    }

    /**
     * 建立与指定用户的 SSE 连接
     *
     * @param userId 用户的唯一标识符，用于区分不同用户的连接
     * @param token  用户的唯一令牌，用于识别具体的连接
     * @return 返回一个 SseEmitter 实例，客户端可以通过该实例接收 SSE 事件
     */
    fun connect(userId: Long, token: String): SseEmitter {
        // 从 USER_TOKEN_EMITTERS 中获取或创建当前用户的 SseEmitter 映射表
        // 每个用户可以有多个 SSE 连接，通过 token 进行区分
        val emitters = USER_TOKEN_EMITTERS.computeIfAbsent(userId) { ConcurrentHashMap() }

        // 创建一个新的 SseEmitter 实例，超时时间设置为一天
        // 避免连接之后直接关闭浏览器导致连接停滞
        val emitter = SseEmitter(86400000L)

        emitters[token] = emitter

        // 当 emitter 完成、超时或发生错误时，从映射表中移除对应的 token
        emitter.onCompletion(Runnable {
            val removed = emitters.remove(token)
            removed?.complete()
        })
        emitter.onTimeout(Runnable {
            val removed = emitters.remove(token)
            removed?.complete()
        })
        emitter.onError { e ->
            val removed = emitters.remove(token)
            removed?.complete()
        }

        try {
            // 向客户端发送一条连接成功的事件
            emitter.send(SseEmitter.event().comment("connected"))
        } catch (e: IOException) {
            // 如果发送消息失败，则从映射表中移除 emitter
            emitters.remove(token)
        }

        return emitter
    }

    /**
     * 断开指定用户的 SSE 连接
     *
     * @param userId 用户的唯一标识符，用于区分不同用户的连接
     * @param token  用户的唯一令牌，用于识别具体的连接
     */
    fun disconnect(userId: Long?, token: String?) {
        if (userId == null || token == null) {
            return
        }

        val emitters = USER_TOKEN_EMITTERS[userId]
        if (MapUtil.isNotEmpty(emitters)) {
            try {
                val sseEmitter = emitters?.get(token)
                sseEmitter?.send(SseEmitter.event().comment("disconnected"))
                sseEmitter?.complete()
            } catch (e: Exception) {
                // Ignore exception
            }
            emitters?.remove(token)
        } else {
            USER_TOKEN_EMITTERS.remove(userId)
        }
    }

    /**
     * 订阅SSE消息主题，并提供一个消费者函数来处理接收到的消息
     *
     * @param consumer 处理SSE消息的消费者函数
     */
    fun subscribeMessage(consumer: (SseMessageDto) -> Unit) {
        RedisUtils.subscribe(SSE_TOPIC, SseMessageDto::class.java, consumer)
    }

    /**
     * 向指定的用户会话发送消息
     *
     * @param userId  要发送消息的用户id
     * @param message 要发送的消息内容
     */
    fun sendMessage(userId: Long, message: String) {
        val emitters = USER_TOKEN_EMITTERS[userId]
        if (MapUtil.isNotEmpty(emitters)) {
            emitters?.forEach { (key, value) ->
                try {
                    value.send(
                        SseEmitter.event()
                            .name("message")
                            .data(message)
                    )
                } catch (e: Exception) {
                    val removed = emitters.remove(key)
                    removed?.complete()
                }
            }
        } else {
            USER_TOKEN_EMITTERS.remove(userId)
        }
    }

    /**
     * 本机全用户会话发送消息
     *
     * @param message 要发送的消息内容
     */
    fun sendMessage(message: String) {
        for (userId in USER_TOKEN_EMITTERS.keys) {
            sendMessage(userId, message)
        }
    }

    /**
     * 发布SSE订阅消息
     *
     * @param sseMessageDto 要发布的SSE消息对象
     */
    fun publishMessage(sseMessageDto: SseMessageDto) {
        val broadcastMessage = SseMessageDto()
        broadcastMessage.message = sseMessageDto.message
        broadcastMessage.userIds = sseMessageDto.userIds
        RedisUtils.publish(SSE_TOPIC, broadcastMessage) { consumer ->
            log.info(
                "SSE发送主题订阅消息topic:{} session keys:{} content:{}",
                SSE_TOPIC, broadcastMessage.userIds, JSONUtil.toJsonStr(broadcastMessage.message)
            )
        }
    }

    /**
     * 向所有的用户发布订阅的消息(群发)
     *
     * @param message 要发布的消息内容
     */
    fun publishAll(message: String) {
        val broadcastMessage = SseMessageDto()
        broadcastMessage.message = message
        RedisUtils.publish(SSE_TOPIC, broadcastMessage) { consumer ->
            log.info("SSE发送主题订阅消息topic:{} content:{}", SSE_TOPIC, message)
        }
    }
}
