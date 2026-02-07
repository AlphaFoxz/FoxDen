package com.github.alphafoxz.foxden.common.mail.config

import cn.hutool.extra.mail.MailAccount
import com.github.alphafoxz.foxden.common.mail.config.properties.MailProperties
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

/**
 * JavaMail 配置
 *
 * @author Michelle.Chung
 */
@AutoConfiguration
@EnableConfigurationProperties(MailProperties::class)
class MailConfig {

    @Bean
    @ConditionalOnProperty(value = ["mail.enabled"], havingValue = "true", matchIfMissing = false)
    fun mailAccount(mailProperties: MailProperties): MailAccount {
        return MailAccount().apply {
            host = mailProperties.host
            port = mailProperties.port
            mailProperties.auth?.let { setAuth(it) }
            socketFactoryPort = mailProperties.port ?: 465
            mailProperties.starttlsEnable?.let { setStarttlsEnable(it) }
            mailProperties.sslEnable?.let { setSslEnable(it) }
            mailProperties.timeout?.let { setTimeout(it.toLong()) }
            mailProperties.connectionTimeout?.let { setConnectionTimeout(it.toLong()) }
        }
    }
}
