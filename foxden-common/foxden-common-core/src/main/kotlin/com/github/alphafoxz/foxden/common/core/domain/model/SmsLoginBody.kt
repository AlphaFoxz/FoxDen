package com.github.alphafoxz.foxden.common.core.domain.model

import jakarta.validation.constraints.NotBlank

/**
 * 短信登录对象
 */
class SmsLoginBody(
    /**
     * 手机号
     */
    @get:NotBlank(message = "{user.phonenumber.not.blank}")
    var phonenumber: String? = null,

    /**
     * 短信code
     */
    @get:NotBlank(message = "{sms.code.not.blank}")
    var smsCode: String? = null
) : LoginBody() {
    companion object {
        private const val serialVersionUID = 1L
    }
}
