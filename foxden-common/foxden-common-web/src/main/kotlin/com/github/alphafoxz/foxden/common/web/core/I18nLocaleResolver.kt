package com.github.alphafoxz.foxden.common.web.core

import org.springframework.web.servlet.LocaleResolver
import org.springframework.lang.Nullable
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.Locale

/**
 * 获取请求头国际化信息
 */
class I18nLocaleResolver : LocaleResolver {
    override fun resolveLocale(request: HttpServletRequest): Locale {
        val language = request.getHeader("content-language")
        val locale = Locale.getDefault()
        if (!language.isNullOrEmpty()) {
            val split = language.split("_")
            return Locale(split[0], split[1])
        }
        return locale
    }

    override fun setLocale(
        request: HttpServletRequest,
        @Nullable response: HttpServletResponse?,
        @Nullable locale: Locale?
    ) {
        // Do nothing
    }
}
