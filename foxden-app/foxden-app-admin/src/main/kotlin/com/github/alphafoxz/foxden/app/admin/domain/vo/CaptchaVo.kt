package com.github.alphafoxz.foxden.app.admin.domain.vo

/**
 * 验证码信息
 *
 * @author FoxDen Team
 */
data class CaptchaVo(
    /**
     * 是否开启验证码
     */
    var captchaEnabled: Boolean = true,

    var uuid: String? = null,

    /**
     * 验证码图片
     */
    var img: String? = null
)
