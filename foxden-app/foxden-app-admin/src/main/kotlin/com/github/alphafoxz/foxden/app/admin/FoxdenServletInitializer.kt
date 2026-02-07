package com.github.alphafoxz.foxden.app.admin

import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

/**
 * Web容器中进行部署（WAR包部署支持）
 *
 * @author FoxDen Team
 */
class FoxdenServletInitializer : SpringBootServletInitializer() {

    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        return application.sources(FoxdenAdminApplication::class.java)
    }
}
