package com.github.alphafoxz.foxden.common.core.constant

/**
 * 全局的key常量 (业务无关的key)
 */
object GlobalConstants {
    /** 全局 redis key (业务无关的key) */
    const val GLOBAL_REDIS_KEY = "global:"

    /** 验证码 redis key */
    const val CAPTCHA_CODE_KEY = "${GLOBAL_REDIS_KEY}captcha_codes:"

    /** 防重提交 redis key */
    const val REPEAT_SUBMIT_KEY = "${GLOBAL_REDIS_KEY}repeat_submit:"

    /** 限流 redis key */
    const val RATE_LIMIT_KEY = "${GLOBAL_REDIS_KEY}rate_limit:"

    /** 三方认证 redis key */
    const val SOCIAL_AUTH_CODE_KEY = "${GLOBAL_REDIS_KEY}social_auth_codes:"
}
