package com.github.alphafoxz.foxden.common.translation.annotation

import org.springframework.stereotype.Component

/**
 * 翻译类型注解
 * 标注在实现类上，用于指定翻译类型
 *
 * @author Lion Li
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class TranslationType(
    /**
     * 翻译类型，需与 {@link Translation} 注解的 type 对应
     */
    val type: String
)
