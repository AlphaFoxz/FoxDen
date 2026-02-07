package com.github.alphafoxz.foxden.common.web.enums

import cn.hutool.captcha.AbstractCaptcha
import cn.hutool.captcha.CircleCaptcha
import cn.hutool.captcha.LineCaptcha
import cn.hutool.captcha.ShearCaptcha

/**
 * 验证码类别
 */
enum class CaptchaCategory(val clazz: Class<out AbstractCaptcha>) {
    /**
     * 线段干扰
     */
    LINE(LineCaptcha::class.java),

    /**
     * 圆圈干扰
     */
    CIRCLE(CircleCaptcha::class.java),

    /**
     * 扭曲干扰
     */
    SHEAR(ShearCaptcha::class.java);

    companion object {
        /**
         * 根据值获取枚举
         *
         * @param value 枚举值
         * @return 枚举实例
         */
        @JvmStatic
        fun newInstance(value: String?): CaptchaCategory? {
            if (value.isNullOrBlank()) {
                return null
            }
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    /**
     * 创建AbstractCaptcha实例
     *
     * @return AbstractCaptcha实例
     */
    fun newInstance(): AbstractCaptcha {
        return clazz.getDeclaredConstructor().newInstance()
    }
}
