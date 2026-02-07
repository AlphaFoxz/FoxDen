package com.github.alphafoxz.foxden.common.core.domain.model

/**
 * 小程序登录用户身份权限
 */
class XcxLoginUser(
    /**
     * openid
     */
    var openid: String? = null
) : LoginUser() {
    companion object {
        private const val serialVersionUID = 1L
    }
}
