package com.github.alphafoxz.foxden.domain.system.bo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

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
    @get:Size(min = 0, max = 64, message = "客户端key长度不能超过{max}个字符")
    var clientKey: String? = null,

    /**
     * 客户端秘钥
     */
    @get:NotBlank(message = "客户端秘钥不能为空")
    @get:Size(min = 0, max = 64, message = "客户端秘钥长度不能超过{max}个字符")
    var clientSecret: String? = null,

    /**
     * 客户端名称
     */
    @get:NotBlank(message = "客户端名称不能为空")
    @get:Size(min = 0, max = 64, message = "客户端名称长度不能超过{max}个字符")
    var clientName: String? = null,

    /**
     * 客户端类型
     */
    @get:NotBlank(message = "客户端类型不能为空")
    var clientType: String? = null,

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
     * 回调地址
     */
    var redirectUri: String? = null,

    /**
     * 授权自动登录
     */
    var autoApprove: Boolean? = null,

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
    var status: String? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 创建者
     */
    var createBy: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
