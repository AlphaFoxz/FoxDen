package com.github.alphafoxz.foxden.domain.system.bo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * OSS对象存储业务对象 sys_oss
 *
 * @author Lion Li
 */
data class SysOssBo(
    /**
     * 对象存储主键
     */
    var ossId: Long? = null,

    /**
     * 文件名
     */
    @get:NotBlank(message = "文件名不能为空")
    @get:Size(min = 0, max = 255, message = "文件名长度不能超过{max}个字符")
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
    @get:Size(min = 0, max = 500, message = "URL地址长度不能超过{max}个字符")
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
