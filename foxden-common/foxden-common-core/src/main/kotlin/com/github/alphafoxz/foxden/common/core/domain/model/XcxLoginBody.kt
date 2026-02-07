package com.github.alphafoxz.foxden.common.core.domain.model

import jakarta.validation.constraints.NotBlank

/**
 * 小程序登录对象
 */
class XcxLoginBody(
    /**
     * 小程序id(多个小程序时使用)
     */
    var appid: String? = null,

    /**
     * 小程序code
     */
    @get:NotBlank(message = "{xcx.code.not.blank}")
    var xcxCode: String? = null
) : LoginBody() {
    companion object {
        private const val serialVersionUID = 1L
    }
}
