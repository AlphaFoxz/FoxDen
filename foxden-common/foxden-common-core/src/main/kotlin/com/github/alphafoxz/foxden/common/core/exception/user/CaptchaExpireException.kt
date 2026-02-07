package com.github.alphafoxz.foxden.common.core.exception.user

import com.github.alphafoxz.foxden.common.core.exception.BaseException

/**
 * 验证码失效异常类
 */
class CaptchaExpireException : BaseException {
    constructor(code: String? = null, vararg args: Any?) : super("user", code ?: "user.jcaptcha.expire", args.toList().toTypedArray(), null)

    companion object {
        private const val serialVersionUID = 1L
    }
}
