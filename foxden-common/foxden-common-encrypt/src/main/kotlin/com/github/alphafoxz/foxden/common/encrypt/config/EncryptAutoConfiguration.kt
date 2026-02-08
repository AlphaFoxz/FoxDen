package com.github.alphafoxz.foxden.common.encrypt.config

import com.github.alphafoxz.foxden.common.encrypt.filter.CryptoFilter
import com.github.alphafoxz.foxden.common.encrypt.properties.ApiDecryptProperties
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import jakarta.servlet.Filter

/**
 * 加密自动配置
 *
 * @author wdhcr
 */
@AutoConfiguration
@EnableConfigurationProperties(ApiDecryptProperties::class)
@ConditionalOnProperty(prefix = "api-decrypt", name = ["enabled"], havingValue = "true", matchIfMissing = false)
class EncryptAutoConfiguration {

    @Bean
    fun cryptoFilter(properties: ApiDecryptProperties): FilterRegistrationBean<Filter> {
        val registrationBean = FilterRegistrationBean<Filter>(CryptoFilter(properties))
        registrationBean.urlPatterns = listOf("/*")
        registrationBean.order = 1
        return registrationBean
    }
}
