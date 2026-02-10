package com.github.alphafoxz.foxden.domain.system.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 对象存储配置视图对象 sys_oss_config
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SysOssConfigVo(
    /**
     * 配置主键
     */
    var ossConfigId: Long? = null,

    /**
     * 配置名称
     */
    var configName: String? = null,

    /**
     * 配置key
     */
    var configKey: String? = null,

    /**
     * 访问key
     */
    var accessKey: String? = null,

    /**
     * 密钥
     */
    var secretKey: String? = null,

    /**
     * 桶名称
     */
    var bucketName: String? = null,

    /**
     * 前缀
     */
    var prefix: String? = null,

    /**
     * 访问站点
     */
    var endpoint: String? = null,

    /**
     * 是否域名（0否 1是）
     */
    var isDomain: Boolean? = null,

    /**
     * 是否https（0否 1是）
     */
    var isHttps: Boolean? = null,

    /**
     * 区域
     */
    var region: String? = null,

    /**
     * 状态（0正常 1停用）
     */
    var status: String? = null,

    /**
     * 扩展字段
     */
    var ext: String? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null,

    /**
     * 桶权限类型(0private 1public 2custom)
     */
    var accessPolicy: String? = null
)
