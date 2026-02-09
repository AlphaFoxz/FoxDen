package com.github.alphafoxz.foxden.domain.system.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * OSS上传返回对象
 *
 * @author FoxDen Team
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SysOssUploadVo(
    /**
     * 文件URL
     */
    var url: String? = null,

    /**
     * 文件名称
     */
    var fileName: String? = null,

    /**
     * OSS对象ID
     */
    var ossId: String? = null,

    /**
     * 原始文件名
     */
    var originalName: String? = null
)
