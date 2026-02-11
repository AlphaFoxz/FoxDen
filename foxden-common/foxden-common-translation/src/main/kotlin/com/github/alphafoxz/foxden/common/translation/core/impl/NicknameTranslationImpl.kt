package com.github.alphafoxz.foxden.common.translation.core.impl

import com.github.alphafoxz.foxden.common.core.service.UserService
import com.github.alphafoxz.foxden.common.translation.annotation.TranslationType
import com.github.alphafoxz.foxden.common.translation.constant.TransConstant
import com.github.alphafoxz.foxden.common.translation.core.TranslationInterface

/**
 * 用户昵称翻译实现
 *
 * @author Lion Li
 */
@TranslationType(type = TransConstant.USER_ID_TO_NICKNAME)
class NicknameTranslationImpl(
    private val userService: UserService
) : TranslationInterface<String> {

    override fun translation(key: Any?, other: String): String? {
        if (key is String) {
            return userService.selectNicknameByIds(key)
        } else if (key is Long) {
            return userService.selectNicknameByIds(key.toString())
        }
        return null
    }
}
