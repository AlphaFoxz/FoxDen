package com.github.alphafoxz.foxden.common.encrypt.annotation

/**
 * 强制加密注解
 *
 * 用于标记需要进行加密传输的API接口
 * 配合 CryptoFilter 使用，自动对请求体进行解密，对响应体进行加密
 *
 * @author Michelle.Chung
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiEncrypt(
    /**
     * 响应加密忽略，默认不加密，为 true 时加密
     */
    val response: Boolean = false
)
