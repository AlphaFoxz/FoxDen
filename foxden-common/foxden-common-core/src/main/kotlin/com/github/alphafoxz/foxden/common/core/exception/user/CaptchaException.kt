package com.github.alphafoxz.foxden.common.core.exception.user

import com.github.alphafoxz.foxden.common.core.exception.BaseException

/**
 * 验证码错误异常类
 */
class CaptchaException : BaseException {
    constructor(code: String, vararg args: Any?) : super("user", code, args.toList().toTypedArray(), null)

    companion object {
        private const val serialVersionUID = 1L
    }
}
