package com.github.alphafoxz.foxden.common.doc.config

import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.doc.config.properties.SpringDocProperties
import com.github.alphafoxz.foxden.common.doc.handler.OpenApiHandler
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Paths
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springdoc.core.configuration.SpringDocConfiguration
import org.springdoc.core.customizers.OpenApiBuilderCustomizer
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springdoc.core.customizers.ServerBaseUrlCustomizer
import org.springdoc.core.properties.SpringDocConfigProperties
import org.springdoc.core.providers.JavadocProvider
import org.springdoc.core.service.OpenAPIService
import org.springdoc.core.service.SecurityService
import org.springdoc.core.utils.PropertyResolverUtils
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import java.util.Optional

/**
 * 接口文档配置
 *
 * @author Lion Li
 */
@AutoConfiguration(before = [SpringDocConfiguration::class])
@EnableConfigurationProperties(SpringDocProperties::class)
@ConditionalOnProperty(
    name = ["springdoc.api-docs.enabled"],
    havingValue = "true",
    matchIfMissing = true
)
class SpringDocConfig(
    private val serverProperties: ServerProperties
) {

    @Bean
    @ConditionalOnMissingBean(OpenAPI::class)
    fun openApi(properties: SpringDocProperties): OpenAPI {
        val openApi = OpenAPI()
        // 文档基本信息
        val infoProperties = properties.info
        val info = convertInfo(infoProperties)
        openApi.info = info
        // 扩展文档信息
        openApi.externalDocs = properties.externalDocs
        openApi.tags = properties.tags
        openApi.paths = properties.paths
        val components = properties.components
        if (components != null) {
            openApi.components = components
            val keySet = components.securitySchemes.keys
            val list = ArrayList<SecurityRequirement>()
            val securityRequirement = SecurityRequirement()
            keySet.forEach(securityRequirement::addList)
            list.add(securityRequirement)
            openApi.security = list
        }
        return openApi
    }

    private fun convertInfo(infoProperties: SpringDocProperties.InfoProperties): Info {
        val info = Info()
        info.title = infoProperties.title
        info.description = infoProperties.description
        info.contact = infoProperties.contact
        info.license = infoProperties.license
        info.version = infoProperties.version
        return info
    }

    /**
     * 自定义 openapi 处理器
     */
    @Bean
    fun openApiBuilder(
        openAPI: Optional<OpenAPI>,
        securityParser: SecurityService,
        springDocConfigProperties: SpringDocConfigProperties,
        propertyResolverUtils: PropertyResolverUtils,
        openApiBuilderCustomisers: Optional<List<OpenApiBuilderCustomizer>>,
        serverBaseUrlCustomisers: Optional<List<ServerBaseUrlCustomizer>>,
        javadocProvider: Optional<JavadocProvider>
    ): OpenAPIService {
        return OpenApiHandler(
            openAPI,
            securityParser,
            springDocConfigProperties,
            propertyResolverUtils,
            openApiBuilderCustomisers,
            serverBaseUrlCustomisers,
            javadocProvider
        )
    }

    /**
     * 对已经生成好的 OpenApi 进行自定义操作
     */
    @Bean
    fun openApiCustomizer(): OpenApiCustomizer {
        val contextPath = serverProperties.servlet.contextPath
        val finalContextPath: String = if (StringUtils.isBlank(contextPath) || "/" == contextPath) {
            ""
        } else {
            contextPath
        }
        // 对所有路径增加前置上下文路径
        return OpenApiCustomizer { openApi: OpenAPI ->
            val oldPaths = openApi.paths
            if (oldPaths is PlusPaths) {
                return@OpenApiCustomizer
            }
            val newPaths = PlusPaths()
            oldPaths.forEach { (k, v) -> newPaths.addPathItem(finalContextPath + k, v) }
            openApi.paths = newPaths
        }
    }

    /**
     * 单独使用一个类便于判断 解决springdoc路径拼接重复问题
     *
     * @author Lion Li
     */
    class PlusPaths : Paths()
}
