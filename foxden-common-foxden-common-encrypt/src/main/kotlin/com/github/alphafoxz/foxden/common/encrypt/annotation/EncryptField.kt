package com.github.alphafoxz.foxden.common.encrypt.annotation

/**
 * 字段加密注解
 *
 * 用于标记需要进行加密存储的字段
 *
 * @author wdhcr
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class EncryptField
