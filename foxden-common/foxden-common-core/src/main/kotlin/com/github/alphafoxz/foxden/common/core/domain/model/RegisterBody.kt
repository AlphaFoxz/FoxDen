package com.github.alphafoxz.foxden.common.core.domain.model

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

/**
 * 用户注册对象
 */
class RegisterBody(
    /**
     * 用户名
     */
    @get:NotBlank(message = "{user.username.not.blank}")
    @get:Length(min = 2, max = 30, message = "{user.username.length.valid}")
    var username: String? = null,

    /**
     * 用户密码
     */
    @get:NotBlank(message = "{user.password.not.blank}")
    @get:Length(min = 5, max = 30, message = "{user.password.length.valid}")
    var password: String? = null,

    /**
     * 用户类型
     */
    var userType: String? = null
) : LoginBody() {
    companion object {
        private const val serialVersionUID = 1L
    }
}
