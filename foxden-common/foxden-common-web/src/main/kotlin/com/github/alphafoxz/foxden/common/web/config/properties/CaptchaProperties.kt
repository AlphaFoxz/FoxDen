package com.github.alphafoxz.foxden.common.web.config.properties

import com.github.alphafoxz.foxden.common.web.enums.CaptchaCategory
import com.github.alphafoxz.foxden.common.web.enums.CaptchaType
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 验证码配置属性
 */
@ConfigurationProperties(prefix = "captcha")
data class CaptchaProperties(
    /**
     * 验证码开关
     */
    var enable: Boolean = true,

    /**
     * 验证码类型
     */
    var type: CaptchaType = CaptchaType.MATH,

    /**
     * 验证码类别
     */
    var category: CaptchaCategory = CaptchaCategory.LINE,

    /**
     * 数字验证码位数
     */
    var numberLength: Int = 2,

    /**
     * 字符验证码长度
     */
    var charLength: Int = 4
)
