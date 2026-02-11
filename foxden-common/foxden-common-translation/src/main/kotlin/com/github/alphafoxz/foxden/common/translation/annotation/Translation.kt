package com.github.alphafoxz.foxden.common.translation.annotation

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.github.alphafoxz.foxden.common.translation.core.handler.TranslationHandler

/**
 * 通用翻译注解
 *
 * @author Lion Li
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@JacksonAnnotationsInside
@JsonSerialize(using = TranslationHandler::class)
annotation class Translation(
    /**
     * 类型 (需与实现类上的 {@link TranslationType} 注解type对应)
     *
     * 默认取当前字段的值 如果设置了 {@link Translation.mapper} 则取映射字段的值
     */
    val type: String,

    /**
     * 映射字段 (如果不为空则取此字段的值)
     */
    val mapper: String = "",

    /**
     * 其他条件 例如: 字典type(sys_user_sex)
     */
    val other: String = ""
)
