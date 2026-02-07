package com.github.alphafoxz.foxden.common.core.domain

import com.github.alphafoxz.foxden.common.core.constant.HttpStatus
import java.io.Serializable

/**
 * 响应信息主体
 */
data class R<T>(
    /** 状态码 */
    var code: Int = 0,

    /** 返回消息 */
    var msg: String? = null,

    /** 返回数据 */
    var data: T? = null
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L

        /** 成功状态码 */
        const val SUCCESS = 200

        /** 失败状态码 */
        const val FAIL = 500

        /**
         * 成功
         */
        fun <T> ok(): R<T> = restResult(null, SUCCESS, "操作成功")

        /**
         * 成功，带数据
         */
        fun <T> ok(data: T?): R<T> = restResult(data, SUCCESS, "操作成功")

        /**
         * 成功，带消息
         */
        fun <T> ok(msg: String): R<T> = restResult(null, SUCCESS, msg)

        /**
         * 成功，带消息和数据
         */
        fun <T> ok(msg: String, data: T?): R<T> = restResult(data, SUCCESS, msg)

        /**
         * 失败
         */
        fun <T> fail(): R<T> = restResult(null, FAIL, "操作失败")

        /**
         * 失败，带消息
         */
        fun <T> fail(msg: String): R<T> = restResult(null, FAIL, msg)

        /**
         * 失败，带数据
         */
        fun <T> fail(data: T): R<T> = restResult(data, FAIL, "操作失败")

        /**
         * 失败，带消息和数据
         */
        fun <T> fail(msg: String, data: T?): R<T> = restResult(data, FAIL, msg)

        /**
         * 失败，带状态码和消息
         */
        fun <T> fail(code: Int, msg: String): R<T> = restResult(null, code, msg)

        /**
         * 返回警告消息
         */
        fun <T> warn(msg: String): R<T> = restResult(null, HttpStatus.WARN, msg)

        /**
         * 返回警告消息，带数据
         */
        fun <T> warn(msg: String, data: T?): R<T> = restResult(data, HttpStatus.WARN, msg)

        /**
         * 判断是否为错误
         */
        fun <T> isError(ret: R<T>): Boolean = !isSuccess(ret)

        /**
         * 判断是否成功
         */
        fun <T> isSuccess(ret: R<T>): Boolean = SUCCESS == ret.code

        private fun <T> restResult(data: T?, code: Int, msg: String): R<T> {
            return R(code = code, data = data, msg = msg)
        }
    }
}
