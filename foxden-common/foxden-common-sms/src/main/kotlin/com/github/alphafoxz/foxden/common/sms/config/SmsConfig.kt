package com.github.alphafoxz.foxden.common.sms.config

import com.github.alphafoxz.foxden.common.sms.core.dao.PlusSmsDao
import com.github.alphafoxz.foxden.common.sms.handler.SmsExceptionHandler
import org.dromara.sms4j.api.dao.SmsDao
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

/**
 * 短信配置类
 *
 * @author Feng
 */
@AutoConfiguration(after = [RedisAutoConfiguration::class])
@ConditionalOnMissingBean(name = ["smsDao"])
class FoxdenSmsConfig {

    @Primary
    @Bean
    fun foxdenSmsDao(): SmsDao {
        return PlusSmsDao()
    }

    /**
     * 异常处理器
     */
    @Bean
    fun smsExceptionHandler(): SmsExceptionHandler {
        return SmsExceptionHandler()
    }
}
