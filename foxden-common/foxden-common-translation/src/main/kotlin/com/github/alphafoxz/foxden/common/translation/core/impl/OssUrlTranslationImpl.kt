package com.github.alphafoxz.foxden.common.translation.core.impl

import com.github.alphafoxz.foxden.common.core.service.OssService
import com.github.alphafoxz.foxden.common.translation.annotation.TranslationType
import com.github.alphafoxz.foxden.common.translation.constant.TransConstant
import com.github.alphafoxz.foxden.common.translation.core.TranslationInterface

/**
 * OSS翻译实现
 *
 * @author Lion Li
 */
@TranslationType(type = TransConstant.OSS_ID_TO_URL)
class OssUrlTranslationImpl(
    private val ossService: OssService
) : TranslationInterface<String> {

    override fun translation(key: Any?, other: String): String? {
        if (key is String) {
            return ossService.selectUrlByIds(key)
        } else if (key is Long) {
            return ossService.selectUrlByIds(key.toString())
        }
        return null
    }
}
