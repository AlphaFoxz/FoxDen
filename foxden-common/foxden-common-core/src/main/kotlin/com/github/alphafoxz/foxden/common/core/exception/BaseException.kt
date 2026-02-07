package com.github.alphafoxz.foxden.common.core.exception

import com.github.alphafoxz.foxden.common.core.utils.MessageUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils

/**
 * 基础异常
 */
open class BaseException : RuntimeException {
    /**
     * 所属模块
     */
    val module: String?

    /**
     * 错误码
     */
    val code: String?

    /**
     * 错误码对应的参数
     */
    val args: Array<Any?>?

    /**
     * 错误消息
     */
    val defaultMessage: String?

    constructor(module: String?, code: String?, args: Array<Any?>?, defaultMessage: String?) {
        this.module = module
        this.code = code
        this.args = args
        this.defaultMessage = defaultMessage
    }

    constructor(module: String, code: String, args: Array<Any?>?) : this(module, code, args, null)

    constructor(module: String, defaultMessage: String) : this(module, null, null, defaultMessage)

    constructor(code: String, args: Array<Any?>?) : this(null, code, args, null)

    constructor(defaultMessage: String) : this(null, null, null, defaultMessage)

    override val message: String?
        get() {
            var msg: String? = null
            if (!StringUtils.isEmpty(code)) {
                msg = MessageUtils.message(code, *args ?: emptyArray())
            }
            if (msg == null) {
                msg = defaultMessage
            }
            return msg
        }

    companion object {
        private const val serialVersionUID = 1L
    }
}
