package com.github.alphafoxz.foxden.app.admin.config

import com.github.alphafoxz.foxden.app.admin.config.ApplicationConfig.FoxDenThreadFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

@Configuration
class ThreadPoolConfig {
    /**
     * 定时任务线程池
     */
    @Bean
    fun scheduledExecutorService(): ScheduledExecutorService {
        return Executors.newScheduledThreadPool(10, FoxDenThreadFactory())
    }
}
