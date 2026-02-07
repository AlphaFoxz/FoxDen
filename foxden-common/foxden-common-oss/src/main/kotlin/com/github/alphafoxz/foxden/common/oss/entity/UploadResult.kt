package com.github.alphafoxz.foxden.common.oss.entity

/**
 * 上传返回体
 */
data class UploadResult(
    /**
     * 文件路径
     */
    var url: String? = null,

    /**
     * 文件名
     */
    var filename: String? = null,

    /**
     * 已上传对象的实体标记（用来校验文件）
     */
    var eTag: String? = null
)
