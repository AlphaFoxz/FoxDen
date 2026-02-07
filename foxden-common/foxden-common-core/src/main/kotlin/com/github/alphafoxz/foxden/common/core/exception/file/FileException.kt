package com.github.alphafoxz.foxden.common.core.exception.file

import com.github.alphafoxz.foxden.common.core.exception.BaseException

/**
 * 文件信息异常类
 */
open class FileException(code: String, args: Array<Any?>) : BaseException("file", code, args, null) {
    companion object {
        private const val serialVersionUID = 1L
    }
}
