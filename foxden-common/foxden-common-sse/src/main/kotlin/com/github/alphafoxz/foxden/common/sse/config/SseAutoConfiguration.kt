package com.github.alphafoxz.foxden.common.sse.config

import com.github.alphafoxz.foxden.common.sse.controller.SseController
import com.github.alphafoxz.foxden.common.sse.core.SseEmitterManager
import com.github.alphafoxz.foxden.common.sse.listener.SseTopicListener
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

/**
 * SSE 自动装配
 *
 * @author FoxDen Team
 */
@AutoConfiguration
@ConditionalOnProperty(value = ["sse.enabled"], havingValue = "true")
@EnableConfigurationProperties(SseProperties::class)
class SseAutoConfiguration {

    @Bean
    fun sseEmitterManager(): SseEmitterManager {
        return SseEmitterManager()
    }

    @Bean
    fun sseTopicListener(sseEmitterManager: SseEmitterManager): SseTopicListener {
        return SseTopicListener().apply {
            this.sseEmitterManager = sseEmitterManager
        }
    }

    @Bean
    fun sseController(sseEmitterManager: SseEmitterManager): SseController {
        return SseController(sseEmitterManager)
    }
}
