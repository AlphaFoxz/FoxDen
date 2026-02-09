package com.github.alphafoxz.foxden.domain.system.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * OSS对象存储视图对象 sys_oss
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SysOssVo(
    /**
     * 对象存储主键
     */
    var ossId: Long? = null,

    /**
     * 文件名
     */
    var fileName: String? = null,

    /**
     * 原始文件名
     */
    var originalName: String? = null,

    /**
     * 文件后缀名
     */
    var fileSuffix: String? = null,

    /**
     * URL地址
     */
    var url: String? = null,

    /**
     * 服务商
     */
    var service: String? = null,

    /**
     * 创建者
     */
    var createBy: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
