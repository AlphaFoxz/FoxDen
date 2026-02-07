package com.github.alphafoxz.foxden.common.core.xss

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

/**
 * 自定义xss校验注解
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [XssValidator::class])
annotation class Xss(
    /**
     * 默认校验失败提示信息
     */
    val message: String = "不允许任何脚本运行",

    /**
     * 分组
     */
    val groups: Array<KClass<*>> = [],

    /**
     * 负载
     */
    val payload: Array<KClass<out Payload>> = []
)
