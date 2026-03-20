package com.github.alphafoxz.foxden.domain.system.bo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

/**
 * 客户端管理业务对象 sys_client
 *
 * @author Lion Li
 */
data class SysClientBo(
    /**
     * 客户端ID
     */
    var id: Long? = null,

    /**
     * 客户端id
     */
    var clientId: String? = null,

    /**
     * 客户端key
     */
    @get:NotBlank(message = "客户端key不能为空")
    var clientKey: String? = null,

    /**
     * 客户端秘钥
     */
    @get:NotBlank(message = "客户端秘钥不能为空")
    var clientSecret: String? = null,

    /**
     * 授权类型
     */
    @get:NotEmpty(message = "授权类型不能为空")
    var grantTypeList: List<String>? = null,

    /**
     * 授权类型（字符串，从 grantTypeList 转换）
     */
    var grantType: String? = null,

    /**
     * 设备类型
     */
    var deviceType: String? = null,

    /**
     * token活跃超时时间
     */
    var activeTimeout: Long? = null,

    /**
     * token固定超时时间
     */
    var timeout: Long? = null,

    /**
     * 状态（0正常 1停用）
     */
    var status: String? = null
)
