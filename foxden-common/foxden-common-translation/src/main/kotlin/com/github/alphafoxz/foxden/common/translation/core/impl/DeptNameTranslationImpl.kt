package com.github.alphafoxz.foxden.common.translation.core.impl

import com.github.alphafoxz.foxden.common.core.service.DeptService
import com.github.alphafoxz.foxden.common.translation.annotation.TranslationType
import com.github.alphafoxz.foxden.common.translation.constant.TransConstant
import com.github.alphafoxz.foxden.common.translation.core.TranslationInterface

/**
 * 部门翻译实现
 *
 * @author Lion Li
 */
@TranslationType(type = TransConstant.DEPT_ID_TO_NAME)
class DeptNameTranslationImpl(
    private val deptService: DeptService
) : TranslationInterface<String> {

    override fun translation(key: Any?, other: String): String? {
        if (key is String) {
            return deptService.selectDeptNameByIds(key)
        } else if (key is Long) {
            return deptService.selectDeptNameByIds(key.toString())
        }
        return null
    }
}
