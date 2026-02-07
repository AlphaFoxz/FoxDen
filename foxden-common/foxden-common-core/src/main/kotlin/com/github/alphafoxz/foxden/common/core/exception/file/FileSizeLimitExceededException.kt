package com.github.alphafoxz.foxden.common.core.exception.file

/**
 * 文件名大小限制异常类
 */
class FileSizeLimitExceededException(defaultMaxSize: Long) : FileException(
    "upload.exceed.maxSize", arrayOf(defaultMaxSize)
) {
    companion object {
        private const val serialVersionUID = 1L
    }
}
