package com.github.alphafoxz.foxden.common.core.exception

import cn.hutool.core.text.StrFormatter

/**
 * 业务异常（支持占位符 {} ）
 */
class ServiceException : RuntimeException {
    /**
     * 错误码
     */
    var code: Int? = null
        private set

    /**
     * 错误提示
     */
    override var message: String? = null
        private set

    /**
     * 错误明细，内部调试错误
     */
    var detailMessage: String? = null
        private set

    constructor()

    constructor(message: String?) {
        this.message = message
    }

    constructor(message: String?, code: Int?) {
        this.message = message
        this.code = code
    }

    constructor(message: String?, vararg args: Any?) {
        this.message = StrFormatter.format(message, *args)
    }

    fun setMessage(message: String?): ServiceException {
        this.message = message
        return this
    }

    fun setDetailMessage(detailMessage: String?): ServiceException {
        this.detailMessage = detailMessage
        return this
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
