package com.github.alphafoxz.foxden.common.oss.exception

/**
 * OSS异常类
 */
class OssException(msg: String?) : RuntimeException(msg) {
    companion object {
        private const val serialVersionUID = 1L
    }
}
