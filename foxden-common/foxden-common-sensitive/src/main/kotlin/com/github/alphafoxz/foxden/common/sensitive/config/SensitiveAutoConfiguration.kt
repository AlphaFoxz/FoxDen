package com.github.alphafoxz.foxden.common.sensitive.config

import com.github.alphafoxz.foxden.common.sensitive.core.SensitiveService
import com.github.alphafoxz.foxden.common.sensitive.impl.DefaultSensitiveServiceImpl
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

/**
 * 数据脱敏自动配置
 *
 * @author Lion Li
 */
@AutoConfiguration
@EnableConfigurationProperties(SensitiveProperties::class)
class SensitiveAutoConfiguration {

    @Bean
    fun sensitiveService(properties: SensitiveProperties): SensitiveService {
        return DefaultSensitiveServiceImpl(properties)
    }
}
