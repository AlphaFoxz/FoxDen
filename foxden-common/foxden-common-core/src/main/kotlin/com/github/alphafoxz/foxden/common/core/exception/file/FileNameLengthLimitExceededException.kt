package com.github.alphafoxz.foxden.common.core.exception.file

/**
 * 文件名称超长限制异常类
 */
class FileNameLengthLimitExceededException(defaultFileNameLength: Int) : FileException(
    "upload.filename.exceed.length", arrayOf(defaultFileNameLength)
) {
    companion object {
        private const val serialVersionUID = 1L
    }
}
