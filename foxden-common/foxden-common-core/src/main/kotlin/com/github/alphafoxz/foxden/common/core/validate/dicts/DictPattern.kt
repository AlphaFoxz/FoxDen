package com.github.alphafoxz.foxden.common.core.validate.dicts

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

/**
 * 字典项校验注解
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [DictPatternValidator::class])
annotation class DictPattern(
    /**
     * 字典类型，如 "sys_user_sex"
     */
    val dictType: String,

    /**
     * 分隔符
     */
    val separator: String,

    /**
     * 默认校验失败提示信息
     */
    val message: String = "字典值无效",

    val groups: Array<KClass<*>> = [],

    val payload: Array<KClass<out Payload>> = []
)
