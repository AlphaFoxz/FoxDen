package com.github.alphafoxz.foxden.common.web.core

import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.utils.StringUtils

/**
 * Web 层通用数据处理
 */
open class BaseController {

    /**
     * 响应返回结果
     */
    protected fun toAjax(rows: Int): R<Void> {
        return if (rows > 0) R.ok() else R.fail()
    }

    /**
     * 响应返回结果
     */
    protected fun toAjax(result: Boolean): R<Void> {
        return if (result) R.ok() else R.fail()
    }

    /**
     * 页面跳转
     */
    fun redirect(url: String?): String {
        return StringUtils.format("redirect:{}", url) ?: "redirect:/"
    }
}
