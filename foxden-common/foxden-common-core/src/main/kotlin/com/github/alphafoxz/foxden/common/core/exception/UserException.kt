package com.github.alphafoxz.foxden.common.core.exception

/**
 * 用户信息异常类
 */
class UserException : BaseException {
    constructor(code: String, vararg args: Any?) : super("user", code, args.toList().toTypedArray(), null)

    companion object {
        private const val serialVersionUID = 1L
    }
}
