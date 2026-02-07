package com.github.alphafoxz.foxden.common.core.domain.model

import jakarta.validation.constraints.NotBlank

/**
 * 三方登录对象
 */
class SocialLoginBody(
    /**
     * 第三方登录平台
     */
    @get:NotBlank(message = "{social.source.not.blank}")
    var source: String? = null,

    /**
     * 第三方登录code
     */
    @get:NotBlank(message = "{social.code.not.blank}")
    var socialCode: String? = null,

    /**
     * 第三方登录socialState
     */
    @get:NotBlank(message = "{social.state.not.blank}")
    var socialState: String? = null
) : LoginBody() {
    companion object {
        private const val serialVersionUID = 1L
    }
}
