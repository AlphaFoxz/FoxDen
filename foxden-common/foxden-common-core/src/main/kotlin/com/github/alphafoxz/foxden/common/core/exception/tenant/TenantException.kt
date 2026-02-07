package com.github.alphafoxz.foxden.common.core.exception.tenant

import com.github.alphafoxz.foxden.common.core.exception.BaseException

/**
 * 租户异常类
 */
class TenantException : BaseException {
    constructor(code: String, vararg args: Any?) : super("tenant", code, args.toList().toTypedArray(), null)

    companion object {
        private const val serialVersionUID = 1L
    }
}
