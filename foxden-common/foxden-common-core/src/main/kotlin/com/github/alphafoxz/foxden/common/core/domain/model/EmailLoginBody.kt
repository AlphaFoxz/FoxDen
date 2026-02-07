package com.github.alphafoxz.foxden.common.core.domain.model

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * 邮件登录对象
 */
class EmailLoginBody(
    /**
     * 邮箱
     */
    @get:NotBlank(message = "{user.email.not.blank}")
    @get:Email(message = "{user.email.not.valid}")
    var email: String? = null,

    /**
     * 邮箱code
     */
    @get:NotBlank(message = "{email.code.not.blank}")
    var emailCode: String? = null
) : LoginBody() {
    companion object {
        private const val serialVersionUID = 1L
    }
}
