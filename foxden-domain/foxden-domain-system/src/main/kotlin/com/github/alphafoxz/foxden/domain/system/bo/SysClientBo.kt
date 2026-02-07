package com.github.alphafoxz.foxden.domain.system.bo

import jakarta.validation.constraints.NotBlank
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
     * 客户端ID
     */
    @get:NotBlank(message = "客户端ID不能为空")
    @get:Size(min = 0, max = 64, message = "客户端ID长度不能超过{max}个字符")
    var clientId: String? = null,

    /**
     * 客户端密钥
     */
    @get:NotBlank(message = "客户端密钥不能为空")
    @get:Size(min = 0, max = 64, message = "客户端密钥长度不能超过{max}个字符")
    var clientKey: String? = null,

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
    var grantType: String? = null,

    /**
     * 回调地址
     */
    var redirectUri: String? = null,

    /**
     * 授权自动登录
     */
    var autoApprove: Boolean? = null,

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
