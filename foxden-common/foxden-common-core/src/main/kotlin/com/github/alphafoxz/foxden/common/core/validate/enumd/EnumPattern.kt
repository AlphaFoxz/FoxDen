package com.github.alphafoxz.foxden.common.core.validate.enumd

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

/**
 * 自定义枚举校验
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.FIELD,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.TYPE
)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [EnumPatternValidator::class])
annotation class EnumPattern(
    /**
     * 需要校验的枚举类型
     */
    val type: KClass<out Enum<*>>,

    /**
     * 枚举类型校验值字段名称
     * 需确保该字段实现了 getter 方法
     */
    val fieldName: String,

    val message: String = "输入值不在枚举范围内",

    val groups: Array<KClass<*>> = [],

    val payload: Array<KClass<out Payload>> = []
)
