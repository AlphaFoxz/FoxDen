package com.github.alphafoxz.foxden.common.core.utils

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.convert.Convert
import cn.hutool.core.lang.Validator
import cn.hutool.core.util.StrUtil
import org.springframework.util.AntPathMatcher
import java.nio.charset.Charset
import java.util.function.Function

/**
 * 字符串工具类
 */
object StringUtils {
    const val SEPARATOR = ","
    const val SLASH = "/"
    const val EMPTY = ""

    /**
     * 获取参数不为空值
     */
    @JvmStatic
    fun blankToDefault(str: String?, defaultValue: String): String =
        StrUtil.blankToDefault(str, defaultValue)

    /**
     * 判断一个字符串是否为空串
     */
    @JvmStatic
    fun isEmpty(str: String?): Boolean = StrUtil.isEmpty(str)

    /**
     * 判断一个字符串是否为非空串
     */
    @JvmStatic
    fun isNotEmpty(str: String?): Boolean = !isEmpty(str)

    /**
     * 去空格
     */
    @JvmStatic
    fun trim(str: String?): String? = StrUtil.trim(str)

    /**
     * 截取字符串
     */
    @JvmStatic
    fun substring(str: String?, start: Int): String? {
        return str?.let { substring(it, start, it.length) }
    }

    /**
     * 截取字符串
     */
    @JvmStatic
    fun substring(str: String?, start: Int, end: Int): String? {
        return str?.let { StrUtil.sub(it, start, end) }
    }

    /**
     * 格式化文本, {} 表示占位符
     */
    @JvmStatic
    fun format(template: String?, vararg params: Any?): String? {
        return StrUtil.format(template, *params)
    }

    /**
     * 是否为http(s)://开头
     */
    @JvmStatic
    fun ishttp(link: String?): Boolean = Validator.isUrl(link)

    /**
     * 字符串转set
     */
    @JvmStatic
    fun str2Set(str: String?, sep: String?): Set<String> {
        return str2List(str, sep, true, false).toSet()
    }

    /**
     * 字符串转list
     */
    @JvmStatic
    fun str2List(str: String?, sep: String?, filterBlank: Boolean, trim: Boolean): List<String> {
        if (isEmpty(str)) return emptyList()

        val separator = sep ?: SEPARATOR
        val strValue = str ?: return emptyList()

        // 过滤空白字符串
        if (filterBlank && isBlank(strValue)) return emptyList()

        return strValue.split(separator)
            .asSequence()
            .filter { s -> !(filterBlank && isBlank(s)) }
            .map { s -> if (trim) trim(s)!! else s }
            .toList()
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串同时串忽略大小写
     */
    @JvmStatic
    fun containsAnyIgnoreCase(cs: CharSequence?, vararg searchCharSequences: CharSequence?): Boolean {
        return StrUtil.containsAnyIgnoreCase(cs, *searchCharSequences)
    }

    /**
     * 驼峰转下划线命名
     */
    @JvmStatic
    fun toUnderScoreCase(str: String?): String? = StrUtil.toUnderlineCase(str)

    /**
     * 是否包含字符串（忽略大小写）
     */
    @JvmStatic
    fun inStringIgnoreCase(str: String?, vararg strs: String?): Boolean {
        return StrUtil.equalsAnyIgnoreCase(str, *strs)
    }

    /**
     * 将下划线大写方式命名的字符串转换为驼峰式
     */
    @JvmStatic
    fun convertToCamelCase(name: String?): String? {
        return name?.let { StrUtil.upperFirst(StrUtil.toCamelCase(it)) }
    }

    /**
     * 驼峰式命名法
     */
    @JvmStatic
    fun toCamelCase(s: String?): String? = StrUtil.toCamelCase(s)

    /**
     * 查找指定字符串是否匹配指定字符串列表中的任意一个字符串
     */
    @JvmStatic
    fun matches(str: String?, strs: List<String>?): Boolean {
        if (isEmpty(str) || CollUtil.isEmpty(strs)) return false
        val strsValue = strs ?: return false
        for (pattern in strsValue) {
            if (isMatch(pattern, str)) return true
        }
        return false
    }

    /**
     * 判断url是否与规则配置
     */
    @JvmStatic
    fun isMatch(pattern: String?, url: String?): Boolean {
        val matcher = AntPathMatcher()
        val patternValue = pattern ?: return false
        val urlValue = url ?: return false
        return matcher.match(patternValue, urlValue)
    }

    /**
     * 数字左边补齐0
     */
    @JvmStatic
    fun padl(num: Number?, size: Int): String? {
        return num?.toString()?.let { padl(it, size, '0') }
    }

    /**
     * 字符串左补齐
     */
    @JvmStatic
    fun padl(s: String?, size: Int, c: Char): String? {
        if (s == null) {
            return Convert.toStr(c).repeat(maxOf(0, size))
        }
        val len = s.length
        return if (len <= size) {
            Convert.toStr(c).repeat(size - len) + s
        } else {
            s.substring(len - size, len)
        }
    }

    /**
     * 切分字符串(分隔符默认逗号)
     */
    @JvmStatic
    fun splitList(str: String?): List<String> {
        return splitTo(str, Convert::toStr)
    }

    /**
     * 切分字符串
     */
    @JvmStatic
    fun splitList(str: String?, separator: String?): List<String> {
        return splitTo(str, separator, Convert::toStr)
    }

    /**
     * 切分字符串
     */
    @JvmStatic
    fun split(str: String?, separator: String?): List<String> {
        if (isBlank(str)) return emptyList()
        val sep = separator ?: SEPARATOR
        return StrUtil.split(str!!, sep)
    }

    /**
     * 切分字符串自定义转换(分隔符默认逗号)
     */
    @JvmStatic
    fun <T : Any> splitTo(str: String?, mapper: Function<in Any?, T?>): List<T> {
        return splitTo(str, SEPARATOR, mapper)
    }

    /**
     * 切分字符串自定义转换
     */
    @JvmStatic
    fun <T : Any> splitTo(str: String?, separator: String?, mapper: Function<in Any?, T?>): List<T> {
        if (isBlank(str)) return emptyList()
        val sep = separator ?: SEPARATOR
        val strValue = str ?: return emptyList()
        @Suppress("UNCHECKED_CAST")
        return StrUtil.split(strValue, sep)
            .stream()
            .filter { obj -> obj != null }
            .map { obj -> mapper.apply(obj) }
            .filter { obj -> obj != null }
            .map { it -> it!! }
            .collect(java.util.stream.Collectors.toList())
    }

    /**
     * 不区分大小写检查 CharSequence 是否以指定的前缀开头
     */
    @JvmStatic
    fun startWithAnyIgnoreCase(str: CharSequence?, vararg prefixs: CharSequence?): Boolean {
        return prefixs.any { prefix -> startsWithIgnoreCase(str, prefix) }
    }

    /**
     * 将字符串从源字符集转换为目标字符集
     */
    @JvmStatic
    fun convert(input: String?, fromCharset: Charset, toCharset: Charset): String? {
        if (isBlank(input)) return input
        return try {
            val bytes = input!!.toByteArray(fromCharset)
            String(bytes, toCharset)
        } catch (e: Exception) {
            input
        }
    }

    /**
     * 将可迭代对象中的元素使用逗号拼接成字符串
     */
    @JvmStatic
    fun joinComma(iterable: Iterable<*>?): String? {
        return join(iterable, SEPARATOR)
    }

    /**
     * 将数组中的元素使用逗号拼接成字符串
     */
    @JvmStatic
    fun joinComma(array: Array<*>?): String? {
        return join(array, SEPARATOR)
    }

    /**
     * 使用指定分隔符拼接可迭代对象
     */
    @JvmStatic
    fun join(iterable: Iterable<*>?, separator: String?): String? {
        if (iterable == null) return null
        return iterable.joinToString(separator ?: SEPARATOR)
    }

    /**
     * 使用指定分隔符拼接数组
     */
    @JvmStatic
    fun join(array: Array<*>?, separator: String?): String? {
        if (array == null) return null
        return array.joinToString(separator ?: SEPARATOR)
    }

    /**
     * 继承 Apache Commons Lang3 的 StringUtils 方法
     */
    @JvmStatic
    fun isBlank(cs: CharSequence?): Boolean = org.apache.commons.lang3.StringUtils.isBlank(cs)

    /**
     * 移除字符串中指定的字符
     */
    @JvmStatic
    fun remove(str: String?, remove: CharSequence?): String {
        if (isBlank(str)) return EMPTY
        if (remove == null) return str!!
        return StrUtil.replace(str!!, remove, EMPTY, true)
    }

    @JvmStatic
    fun isNotBlank(cs: CharSequence?): Boolean = org.apache.commons.lang3.StringUtils.isNotBlank(cs)

    @JvmStatic
    fun equalsIgnoreCase(cs1: CharSequence?, cs2: CharSequence?): Boolean =
        org.apache.commons.lang3.StringUtils.equalsIgnoreCase(cs1, cs2)

    @JvmStatic
    fun startsWithIgnoreCase(str: CharSequence?, prefix: CharSequence?): Boolean =
        org.apache.commons.lang3.StringUtils.startsWithIgnoreCase(str, prefix)

    @JvmStatic
    fun startsWith(str: String?, prefix: String?): Boolean =
        org.apache.commons.lang3.StringUtils.startsWith(str, prefix)

    @JvmStatic
    fun startsWith(str: String?, prefix: String?, ignoreCase: Boolean): Boolean =
        if (ignoreCase) startsWithIgnoreCase(str, prefix) else startsWith(str, prefix)

    @JvmStatic
    fun endsWith(str: String?, suffix: String?): Boolean =
        org.apache.commons.lang3.StringUtils.endsWith(str, suffix)

    @JvmStatic
    fun endsWith(str: String?, suffix: String?, ignoreCase: Boolean): Boolean =
        if (ignoreCase) org.apache.commons.lang3.StringUtils.endsWithIgnoreCase(str, suffix) else endsWith(str, suffix)

    @JvmStatic
    fun containsAny(cs: CharSequence?, searchChars: String?): Boolean =
        org.apache.commons.lang3.StringUtils.containsAny(cs, *searchChars.orEmpty().toCharArray())

    @JvmStatic
    fun containsAny(cs: CharSequence?, vararg searchChars: Char): Boolean =
        org.apache.commons.lang3.StringUtils.containsAny(cs, *searchChars)

    // For CharArray - use different JVM name to avoid signature clash
    @JvmName("containsAnyCharArray")
    fun containsAny(cs: CharSequence?, searchChars: CharArray): Boolean =
        org.apache.commons.lang3.StringUtils.containsAny(cs, *searchChars)

    @JvmStatic
    fun trimToEmpty(str: String?): String =
        org.apache.commons.lang3.StringUtils.trimToEmpty(str)

    @JvmStatic
    fun stripEnd(str: String?, stripChars: String?): String {
        return org.apache.commons.lang3.StringUtils.stripEnd(str, stripChars)
    }

    /**
     * 检查字符串是否包含指定字符串
     *
     * @param str 原字符串
     * @param searchString 要查找的字符串
     * @return 是否包含
     */
    @JvmStatic
    fun contains(str: CharSequence?, searchString: CharSequence?): Boolean {
        return org.apache.commons.lang3.StringUtils.contains(str, searchString)
    }

    /**
     * 检查字符串是否包含指定字符串（忽略大小写）
     *
     * @param str 原字符串
     * @param searchString 要查找的字符串
     * @return 是否包含
     */
    @JvmStatic
    fun containsIgnoreCase(str: CharSequence?, searchString: CharSequence?): Boolean {
        return org.apache.commons.lang3.StringUtils.containsIgnoreCase(str, searchString)
    }
}
