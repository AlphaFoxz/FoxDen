package com.github.alphafoxz.foxden.common.web.config

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.WebDataBinder

/**
 * Web 数据绑定配置
 *
 * 用于防止HTTP请求头污染模型属性（@ModelAttribute）的绑定。
 *
 * 在 Spring Framework 6.2 中，ExtendedServletRequestDataBinder 被更新，
 * 会将 HTTP 请求头绑定到模型属性。这会导致当 Bo 对象的属性名与
 * HTTP 请求头名称相同时，请求头的值会覆盖 URL 参数的值。
 *
 * 此配置通过 @InitBinder 完全禁用 HTTP 请求头的绑定功能，
 * 确保数据只来自请求参数、路径变量等预期的数据源。
 *
 * @since 3.5.11
 */
@AutoConfiguration
@ControllerAdvice
class WebBindingConfig {

    /**
     * 配置数据绑定器，完全禁用 HTTP 请求头绑定
     *
     * 此方法会在所有控制器的方法执行前被调用。
     * 使用反射调用 setHeaderPredicate 方法，完全禁用 HTTP 请求头绑定。
     *
     * 这是 Spring Framework 6.2+ 官方推荐的方式，比 setDisallowedFields 更彻底。
     *
     * @param binder WebDataBinder 实例
     */
    @InitBinder
    fun initBinder(binder: WebDataBinder) {
        // Spring Framework 6.2+ 的 ExtendedServletRequestDataBinder 支持通过 headerPredicate 控制哪些 header 可以被绑定
        // 通过设置为始终返回 false，可以完全禁用 HTTP 请求头绑定
        // 使用反射以兼容不同版本的 Spring Framework
        try {
            val setHeaderPredicateMethod = binder.javaClass.getMethod(
                "setHeaderPredicate",
                java.util.function.Predicate::class.java
            )
            val predicate = java.util.function.Predicate<String> { false }
            setHeaderPredicateMethod.invoke(binder, predicate)
        } catch (e: NoSuchMethodException) {
            // 如果方法不存在，说明不是 ExtendedServletRequestDataBinder 或版本过低，忽略即可
            // 这种情况下回退到 setDisallowedFields
            @Suppress("unused")
            val disallowedFields = arrayOf(
                // FoxDen 项目中可能被污染的常见字段
                "clientid", "clientId",
                // 标准 HTTP 头部（补充保护）
                "accept", "authorization", "cacheControl", "connection",
                "contentLength", "contentType", "cookie", "date", "host",
                "origin", "referer", "userAgent", "priority",
                "xForwardedFor", "xRealIp"
            )
            try {
                val setDisallowedFieldsMethod = binder.javaClass.getMethod(
                    "setDisallowedFields",
                    Array<String>::class.java
                )
                setDisallowedFieldsMethod.invoke(binder, disallowedFields)
            } catch (ex: NoSuchMethodException) {
                // 如果连 setDisallowedFields 都没有，那就真的没办法了
            }
        }
    }
}
