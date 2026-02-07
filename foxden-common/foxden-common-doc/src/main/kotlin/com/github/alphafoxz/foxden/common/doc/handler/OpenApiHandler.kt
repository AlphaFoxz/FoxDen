package com.github.alphafoxz.foxden.common.doc.handler

import cn.hutool.core.io.IoUtil
import io.swagger.v3.core.jackson.TypeNameResolver
import io.swagger.v3.core.util.AnnotationsUtils
import io.swagger.v3.oas.annotations.tags.Tags
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.Paths
import io.swagger.v3.oas.models.tags.Tag
import org.apache.commons.lang3.StringUtils
import com.github.alphafoxz.foxden.common.core.utils.StreamUtils
import org.slf4j.LoggerFactory
import org.springdoc.core.customizers.OpenApiBuilderCustomizer
import org.springdoc.core.customizers.ServerBaseUrlCustomizer
import org.springdoc.core.properties.SpringDocConfigProperties
import org.springdoc.core.providers.JavadocProvider
import org.springdoc.core.service.OpenAPIService
import org.springdoc.core.service.SecurityService
import org.springdoc.core.utils.PropertyResolverUtils
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.util.CollectionUtils
import org.springframework.web.method.HandlerMethod
import java.io.StringReader
import java.lang.reflect.Method
import java.util.*
import java.util.stream.Collectors

/**
 * 自定义 openapi 处理器
 * 对源码功能进行修改 增强使用
 */
class OpenApiHandler(
    openAPI: Optional<OpenAPI>,
    private val securityParser: SecurityService,
    private val springDocConfigProperties: SpringDocConfigProperties,
    private val propertyResolverUtils: PropertyResolverUtils,
    private val openApiBuilderCustomizers: Optional<List<OpenApiBuilderCustomizer>>,
    private val serverBaseUrlCustomizers: Optional<List<ServerBaseUrlCustomizer>>,
    private val javadocProvider: Optional<JavadocProvider>
) : OpenAPIService(
    openAPI,
    securityParser,
    springDocConfigProperties,
    propertyResolverUtils,
    openApiBuilderCustomizers,
    serverBaseUrlCustomizers,
    javadocProvider
) {
    private val log = LoggerFactory.getLogger(OpenApiHandler::class.java)

    private val mappingsMap: MutableMap<String, Any> = HashMap()
    private val springdocTags: MutableMap<HandlerMethod, Tag> = HashMap()
    private val cachedOpenAPI: MutableMap<String, OpenAPI> = HashMap()

    private lateinit var context: ApplicationContext
    private var openAPI: OpenAPI? = null
    private var isServersPresent = false
    private var serverBaseUrl: String? = null

    init {
        if (openAPI.isPresent) {
            this.openAPI = openAPI.get()
            if (this.openAPI?.components == null)
                this.openAPI?.components = Components()
            if (this.openAPI?.paths == null)
                this.openAPI?.paths = Paths()
            if (!CollectionUtils.isEmpty(this.openAPI?.servers))
                isServersPresent = true
        }
        if (springDocConfigProperties.isUseFqn)
            TypeNameResolver.std.setUseFqn(true)
    }

    override fun buildTags(
        handlerMethod: HandlerMethod,
        operation: Operation,
        openAPI: OpenAPI,
        locale: Locale
    ): Operation {
        val tags: MutableSet<Tag> = HashSet()
        val tagsStr: MutableSet<String> = HashSet()

        buildTagsFromMethod(handlerMethod.method, tags, tagsStr, locale)
        buildTagsFromClass(handlerMethod.beanType, tags, tagsStr, locale)

        if (!CollectionUtils.isEmpty(tagsStr)) {
            val resolvedTags = tagsStr.map { s -> propertyResolverUtils.resolve(s, locale) }
            tagsStr.clear()
            tagsStr.addAll(resolvedTags)
        }

        if (springdocTags.containsKey(handlerMethod)) {
            val tag = springdocTags[handlerMethod]!!
            tagsStr.add(tag.name)
            if (openAPI.tags == null || !openAPI.tags.contains(tag)) {
                openAPI.addTagsItem(tag)
            }
        }

        if (!CollectionUtils.isEmpty(tagsStr)) {
            if (CollectionUtils.isEmpty(operation.tags))
                operation.tags = ArrayList(tagsStr)
            else {
                val operationTagsSet: MutableSet<String> = HashSet(operation.tags)
                operationTagsSet.addAll(tagsStr)
                operation.tags.clear()
                operation.tags.addAll(operationTagsSet)
            }
        }

        if (isAutoTagClasses(operation)) {
            if (javadocProvider.isPresent) {
                val description = javadocProvider.get().getClassJavadoc(handlerMethod.beanType)
                if (StringUtils.isNotBlank(description)) {
                    val tag = Tag()

                    // 自定义部分 修改使用java注释当tag名
                    val list: List<String> = IoUtil.readLines(StringReader(description), ArrayList())
                    tag.name = list[0]
                    operation.addTagsItem(list[0])

                    tag.description = description
                    if (openAPI.tags == null || !openAPI.tags.contains(tag)) {
                        openAPI.addTagsItem(tag)
                    }
                }
            } else {
                val tagAutoName = splitCamelCase(handlerMethod.beanType.simpleName)
                operation.addTagsItem(tagAutoName)
            }
        }

        if (!CollectionUtils.isEmpty(tags)) {
            // Existing tags
            val openApiTags = openAPI.tags
            if (!CollectionUtils.isEmpty(openApiTags))
                tags.addAll(openApiTags)
            openAPI.tags = ArrayList(tags)
        }

        // Handle SecurityRequirement at operation level
        val securityRequirements = securityParser
            .getSecurityRequirements(handlerMethod)
        if (securityRequirements != null) {
            if (securityRequirements.isEmpty())
                operation.security = Collections.emptyList()
            else
                securityParser.buildSecurityRequirement(securityRequirements, operation)
        }

        return operation
    }

    private fun buildTagsFromMethod(
        method: Method,
        tags: MutableSet<Tag>,
        tagsStr: MutableSet<String>,
        locale: Locale
    ) {
        // method tags
        val tagsSet: Set<Tags> = AnnotatedElementUtils
            .findAllMergedAnnotations(method, Tags::class.java)
        val methodTags: MutableSet<io.swagger.v3.oas.annotations.tags.Tag> = tagsSet.stream()
            .flatMap { x: Tags -> Arrays.stream(x.value) }
            .collect(Collectors.toSet())
        methodTags.addAll(
            AnnotatedElementUtils.findAllMergedAnnotations(
                method,
                io.swagger.v3.oas.annotations.tags.Tag::class.java
            )
        )
        if (!CollectionUtils.isEmpty(methodTags)) {
            tagsStr.addAll(
                StreamUtils.toSet(methodTags) { tag: io.swagger.v3.oas.annotations.tags.Tag ->
                    propertyResolverUtils.resolve(
                        tag.name,
                        locale
                    )
                }
            )
            val allTags: MutableList<io.swagger.v3.oas.annotations.tags.Tag> = ArrayList(methodTags)
            addTags(allTags, tags, locale)
        }
    }

    private fun addTags(
        sourceTags: List<io.swagger.v3.oas.annotations.tags.Tag>,
        tags: MutableSet<Tag>,
        locale: Locale
    ) {
        val optionalTagSet: Optional<Set<Tag>> = AnnotationsUtils
            .getTags(sourceTags.toTypedArray(), true)
        optionalTagSet.ifPresent { tagsSet ->
            tagsSet.forEach { tag ->
                tag.name(propertyResolverUtils.resolve(tag.name, locale))
                tag.description(propertyResolverUtils.resolve(tag.description, locale))
                if (tags.stream().noneMatch { t -> t.name == tag.name })
                    tags.add(tag)
            }
        }
    }

    companion object {
        fun splitCamelCase(s: String): String {
            return s.replace("([a-z])([A-Z])".toRegex(), "$1-$2").lowercase()
        }
    }
}
