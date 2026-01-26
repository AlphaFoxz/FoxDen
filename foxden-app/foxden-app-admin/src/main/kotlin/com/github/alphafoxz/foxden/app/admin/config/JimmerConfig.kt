package com.github.alphafoxz.foxden.app.admin.config

import com.github.alphafoxz.foxden.domain.system.entity.SysUser
import com.github.alphafoxz.foxden.domain.tenant.entity.SysTenant
import com.github.alphafoxz.foxden.domain.tenant.entity.SysTenantPackage
import org.babyfish.jimmer.sql.runtime.EntityManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JimmerConfig {
    @Bean
    fun entityManager(): EntityManager {
        return EntityManager(
            SysUser::class.java,
            SysTenant::class.java,
            SysTenantPackage::class.java
        )
    }
}
