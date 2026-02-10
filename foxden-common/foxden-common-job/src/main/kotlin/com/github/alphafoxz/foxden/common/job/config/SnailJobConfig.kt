package com.github.alphafoxz.foxden.common.job.config

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import com.aizuda.snailjob.client.common.appender.SnailLogbackAppender
import com.aizuda.snailjob.client.common.event.SnailClientStartingEvent
import com.aizuda.snailjob.client.starter.EnableSnailJob
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * 启动定时任务
 *
 * @author opensnail
 * @date 2024-05-17
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "snail-job", name = ["enabled"], havingValue = "true")
@EnableScheduling
@EnableSnailJob
class SnailJobConfig {

    @EventListener(SnailClientStartingEvent::class)
    fun onStarting(event: SnailClientStartingEvent) {
        val lc = LoggerFactory.getILoggerFactory() as LoggerContext
        val ca = SnailLogbackAppender<ILoggingEvent>()
        ca.name = "snail_log_appender"
        ca.start()
        val rootLogger = lc.getLogger(Logger.ROOT_LOGGER_NAME)
        rootLogger.addAppender(ca)
    }
}
