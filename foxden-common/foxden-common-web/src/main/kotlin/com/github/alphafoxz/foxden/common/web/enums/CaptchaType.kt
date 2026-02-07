package com.github.alphafoxz.foxden.common.web.enums

import cn.hutool.captcha.generator.CodeGenerator
import cn.hutool.captcha.generator.MathGenerator
import cn.hutool.captcha.generator.RandomGenerator

/**
 * 验证码类型
 */
enum class CaptchaType(val clazz: Class<out CodeGenerator>) {
    /**
     * 数字
     */
    MATH(MathGenerator::class.java),

    /**
     * 字符
     */
    CHAR(RandomGenerator::class.java);

    companion object {
        /**
         * 根据值获取枚举
         *
         * @param value 枚举值
         * @return 枚举实例
         */
        @JvmStatic
        fun newInstance(value: String?): CaptchaType? {
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
     * 创建CodeGenerator实例
     *
     * @return CodeGenerator实例
     */
    fun newInstance(): CodeGenerator {
        return clazz.getDeclaredConstructor().newInstance()
    }
}
