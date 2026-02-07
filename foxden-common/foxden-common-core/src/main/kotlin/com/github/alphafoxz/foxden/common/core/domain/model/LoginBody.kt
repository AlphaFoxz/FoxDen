package com.github.alphafoxz.foxden.common.core.domain.model

import jakarta.validation.constraints.NotBlank
import java.io.Serializable

/**
 * 用户登录对象
 */
open class LoginBody(
    /**
     * 客户端id
     */
    @get:NotBlank(message = "{auth.clientid.not.blank}")
    var clientId: String? = null,

    /**
     * 授权类型
     */
    @get:NotBlank(message = "{auth.grant.type.not.blank}")
    var grantType: String? = null,

    /**
     * 租户ID
     */
    var tenantId: String? = null,

    /**
     * 验证码
     */
    var code: String? = null,

    /**
     * 唯一标识
     */
    var uuid: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
