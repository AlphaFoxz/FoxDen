package com.github.alphafoxz.foxden.common.core.utils

import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validator

/**
 * Validator 校验框架工具
 */
object ValidatorUtils {
    private val VALID: Validator by lazy { SpringUtils.getBean(Validator::class.java) }

    /**
     * 对给定对象进行参数校验，并根据指定的校验组进行校验
     *
     * @param object 要进行校验的对象
     * @param groups 校验组
     * @throws ConstraintViolationException 如果校验不通过，则抛出参数校验异常
     */
    @JvmStatic
    fun <T> validate(obj: T, vararg groups: Class<*>) {
        val validate = VALID.validate(obj, *groups)
        if (validate.isNotEmpty()) {
            throw ConstraintViolationException("参数校验异常", validate)
        }
    }
}
