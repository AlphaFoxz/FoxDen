package com.github.alphafoxz.foxden.common.translation.core.impl

import com.github.alphafoxz.foxden.common.core.service.DictService
import com.github.alphafoxz.foxden.common.translation.annotation.TranslationType
import com.github.alphafoxz.foxden.common.translation.constant.TransConstant
import com.github.alphafoxz.foxden.common.translation.core.TranslationInterface

/**
 * 字典翻译实现
 *
 * @author Lion Li
 */
@TranslationType(type = TransConstant.DICT_TYPE_TO_LABEL)
class DictTypeTranslationImpl(
    private val dictService: DictService
) : TranslationInterface<String> {

    override fun translation(key: Any?, other: String): String? {
        // other 参数用于传递字典类型
        val dictType = other
        if (key is String) {
            return dictService.selectDictLabel(dictType, key)
        }
        return null
    }
}
