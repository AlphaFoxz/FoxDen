package com.github.alphafoxz.foxden.app.admin.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * 应用程序配置
 *
 * @author FoxDen Team
 */
@EnableScheduling
@Configuration
class ApplicationConfig {
    /**
     * 自定义线程工厂
     */
    class FoxDenThreadFactory : ThreadFactory {
        private val threadNumber = AtomicInteger(1)

        override fun newThread(r: Runnable): Thread {
            val thread = Thread(
                Thread.currentThread().threadGroup,
                r,
                "foxden-schedule-${threadNumber.getAndIncrement()}",
                0
            )
            if (thread.isDaemon) {
                thread.isDaemon = false
            }
            if (thread.priority != Thread.NORM_PRIORITY) {
                thread.priority = Thread.NORM_PRIORITY
            }
            return thread
        }
    }
}
