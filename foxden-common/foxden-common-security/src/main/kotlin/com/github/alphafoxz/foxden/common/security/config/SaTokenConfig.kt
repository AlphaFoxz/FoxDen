package com.github.alphafoxz.foxden.common.security.config

import cn.dev33.satoken.dao.SaTokenDao
import cn.dev33.satoken.jwt.StpLogicJwtForSimple
import cn.dev33.satoken.stp.StpInterface
import cn.dev33.satoken.stp.StpLogic
import com.github.alphafoxz.foxden.common.security.core.dao.FoxdenSaTokenDao
import com.github.alphafoxz.foxden.common.security.core.service.SaPermissionImpl
import com.github.alphafoxz.foxden.common.security.handler.SaTokenExceptionHandler
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean

/**
 * Sa-Token 配置类
 */
@AutoConfiguration
class SaTokenConfig {

    /**
     * Sa-Token 整合 jwt (简单模式)
     */
    @Bean
    fun stpLogicJwt(): StpLogic {
        return StpLogicJwtForSimple()
    }

    /**
     * 权限接口实现(使用bean注入方便用户替换)
     */
    @Bean
    fun stpInterface(): StpInterface {
        return SaPermissionImpl()
    }

    /**
     * 自定义dao层存储
     */
    @Bean
    fun saTokenDao(): SaTokenDao {
        return FoxdenSaTokenDao
    }

    /**
     * 异常处理器
     */
    @Bean
    fun saTokenExceptionHandler(): SaTokenExceptionHandler {
        return SaTokenExceptionHandler()
    }
}
