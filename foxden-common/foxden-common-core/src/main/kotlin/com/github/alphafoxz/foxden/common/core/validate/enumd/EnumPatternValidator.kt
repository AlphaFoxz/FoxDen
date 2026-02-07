package com.github.alphafoxz.foxden.common.core.validate.enumd

import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.core.utils.reflect.ReflectUtils
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

/**
 * 自定义枚举校验注解实现
 */
class EnumPatternValidator : ConstraintValidator<EnumPattern, String> {
    private lateinit var annotation: EnumPattern

    override fun initialize(annotation: EnumPattern) {
        super.initialize(annotation)
        this.annotation = annotation
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (StringUtils.isNotBlank(value)) {
            val fieldName = annotation.fieldName
            for (e in annotation.type.java.enumConstants) {
                if (value == ReflectUtils.invokeGetter(e, fieldName)) {
                    return true
                }
            }
        }
        return false
    }
}
