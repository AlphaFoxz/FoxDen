package com.github.alphafoxz.foxden.common.sse.listener

import cn.hutool.core.collection.CollUtil
import cn.hutool.json.JSONUtil
import com.github.alphafoxz.foxden.common.sse.core.SseEmitterManager
import com.github.alphafoxz.foxden.common.sse.dto.SseMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.Ordered

/**
 * SSE 主题订阅监听器
 *
 * @author FoxDen Team
 */
class SseTopicListener : ApplicationRunner, Ordered {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var sseEmitterManager: SseEmitterManager

    /**
     * 在Spring Boot应用程序启动时初始化SSE主题订阅监听器
     *
     * @param args 应用程序参数
     */
    override fun run(args: ApplicationArguments) {
        sseEmitterManager.subscribeMessage { message ->
            log.info("SSE主题订阅收到消息session keys={} content={}", message.getUserIds(), JSONUtil.toJsonStr(message.getContent()))
            // 如果key不为空就按照key发消息 如果为空就群发
            if (CollUtil.isNotEmpty(message.getUserIds())) {
                message.getUserIds()?.forEach { key ->
                    sseEmitterManager.sendMessage(key, JSONUtil.toJsonStr(message.getContent()))
                }
            } else {
                sseEmitterManager.sendMessage(JSONUtil.toJsonStr(message.getContent()))
            }
        }
        log.info("初始化SSE主题订阅监听器成功")
    }

    override fun getOrder(): Int {
        return -1
    }
}
