package com.github.alphafoxz.foxden.common.core.domain.dto

import java.io.Serializable

/**
 * OSS对象
 */
data class OssDTO(
    /**
     * 对象存储主键
     */
    var ossId: Long? = null,

    /**
     * 文件名
     */
    var fileName: String? = null,

    /**
     * 原名
     */
    var originalName: String? = null,

    /**
     * 文件后缀名
     */
    var fileSuffix: String? = null,

    /**
     * URL地址
     */
    var url: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
