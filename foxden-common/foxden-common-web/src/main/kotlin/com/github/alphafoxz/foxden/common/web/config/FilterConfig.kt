package com.github.alphafoxz.foxden.common.web.config

import com.github.alphafoxz.foxden.common.web.config.properties.XssProperties
import com.github.alphafoxz.foxden.common.web.filter.RepeatableFilter
import com.github.alphafoxz.foxden.common.web.filter.XssFilter
import jakarta.servlet.DispatcherType
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean

/**
 * Filter 配置
 */
@AutoConfiguration
@EnableConfigurationProperties(XssProperties::class)
class FilterConfig {

    @Bean
    @ConditionalOnProperty(value = ["xss.enabled"], havingValue = "true", matchIfMissing = false)
    fun xssFilter(): FilterRegistrationBean<XssFilter> {
        val registration = FilterRegistrationBean(XssFilter())
        registration.addUrlPatterns("/*")
        registration.order = FilterRegistrationBean.HIGHEST_PRECEDENCE + 1
        registration.setDispatcherTypes(DispatcherType.REQUEST)
        return registration
    }

    @Bean
    fun repeatableFilter(): FilterRegistrationBean<RepeatableFilter> {
        val registration = FilterRegistrationBean(RepeatableFilter())
        registration.addUrlPatterns("/*")
        return registration
    }
}
