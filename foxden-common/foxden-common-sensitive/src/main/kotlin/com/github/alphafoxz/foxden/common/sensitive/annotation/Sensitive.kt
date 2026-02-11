package com.github.alphafoxz.foxden.common.sensitive.annotation

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.github.alphafoxz.foxden.common.sensitive.core.SensitiveStrategy
import com.github.alphafoxz.foxden.common.sensitive.handler.SensitiveHandler

/**
 * 数据脱敏注解
 *
 * @author zhujie
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveHandler::class)
annotation class Sensitive(
    /**
     * 脱敏策略
     */
    val strategy: SensitiveStrategy,

    /**
     * 角色标识符 多个角色满足一个即可
     */
    val roleKey: Array<String> = [],

    /**
     * 权限标识符 多个权限满足一个即可
     */
    val perms: Array<String> = []
)
