package com.github.alphafoxz.foxden.common.core.utils.regex

import cn.hutool.core.util.ReUtil

/**
 * 正则相关工具类
 */
object RegexUtils : ReUtil() {
    /**
     * 从输入字符串中提取匹配的部分，如果没有匹配则返回默认值
     */
    fun extractFromString(input: String?, regex: String, defaultInput: String): String {
        return try {
            val str = ReUtil.get(regex, input, 1)
            str ?: defaultInput
        } catch (e: Exception) {
            defaultInput
        }
    }
}
