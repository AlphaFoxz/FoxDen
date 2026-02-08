package com.github.alphafoxz.foxden.app.admin.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Spring Security 配置
 *
 * @description 配置安全策略，与 Sa-Token 集成
 */
@Configuration
@EnableWebSecurity
class SecurityConfig {

    /**
     * Security 过滤链配置
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            // 禁用 CSRF（Sa-Token 不需要）
            csrf { disable() }

            // 禁用 CORS（开发环境）
            cors { disable() }

            // 禁用 HTTP Basic
            httpBasic { disable() }

            // 禁用表单登录
            formLogin { disable() }

            // 禁用 Logout
            logout { disable() }

            // Session 管理：无状态
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            // 授权配置
            authorizeHttpRequests {
                // 允许匿名访问的路径
                authorize("/auth/**", permitAll)
                authorize("/captchaImage", permitAll)
                authorize("/doc.html", permitAll)
                authorize("/webjars/**", permitAll)
                authorize("/swagger-resources/**", permitAll)
                authorize("/v3/api-docs/**", permitAll)
                authorize("/favicon.ico", permitAll)
                authorize("/error", permitAll)

                // Swagger UI 相关
                authorize("/swagger-ui/**", permitAll)
                authorize("/swagger-ui.html", permitAll)

                // 开发环境：允许所有请求（Sa-Token 会进行权限控制）
                authorize(anyRequest, permitAll)
            }

            // 禁用异常处理的重定向
            exceptionHandling {
                authenticationEntryPoint = null
                accessDeniedHandler = null
            }
        }

        return http.build()
    }
}
