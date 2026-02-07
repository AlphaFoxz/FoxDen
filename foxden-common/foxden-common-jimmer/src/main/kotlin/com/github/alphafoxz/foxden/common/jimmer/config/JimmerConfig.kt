package com.github.alphafoxz.foxden.common.jimmer.config

import com.github.alphafoxz.foxden.common.jimmer.aspect.DataPermissionPointcutAdvisor
import com.github.alphafoxz.foxden.common.jimmer.handler.JimmerExceptionHandler
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Role
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * Jimmer配置类
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
open class JimmerConfig {

    /**
     * 数据权限切面处理器
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    open fun dataPermissionPointcutAdvisor(): DataPermissionPointcutAdvisor {
        return DataPermissionPointcutAdvisor()
    }

    /**
     * Jimmer异常处理器
     */
    @Bean
    open fun jimmerExceptionHandler(): JimmerExceptionHandler {
        return JimmerExceptionHandler()
    }
}

