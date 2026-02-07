package com.github.alphafoxz.foxden.common.core.validate.dicts

import com.github.alphafoxz.foxden.common.core.service.DictService
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

/**
 * 自定义字典值校验器
 */
class DictPatternValidator : ConstraintValidator<DictPattern, String> {
    private lateinit var dictType: String
    private var separator: String = ","

    override fun initialize(annotation: DictPattern) {
        dictType = annotation.dictType
        if (StringUtils.isNotBlank(annotation.separator)) {
            separator = annotation.separator
        }
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (StringUtils.isBlank(dictType) || StringUtils.isBlank(value)) {
            return false
        }
        val dictLabel = SpringUtils.getBean(DictService::class.java).getDictLabel(dictType, value!!, separator)
        return StringUtils.isNotBlank(dictLabel)
    }
}
