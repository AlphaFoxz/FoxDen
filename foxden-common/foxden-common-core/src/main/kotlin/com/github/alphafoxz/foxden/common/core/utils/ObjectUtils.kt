package com.github.alphafoxz.foxden.common.core.utils

import cn.hutool.core.util.ObjectUtil

/**
 * 对象工具类
 */
object ObjectUtils {
    /**
     * 如果对象不为空，则获取对象中的某个字段
     */
    @JvmStatic
    fun <T : Any, E> notNullGetter(obj: T?, func: ((T) -> E)?): E? {
        if (obj != null && func != null) {
            return func(obj)
        }
        return null
    }

    /**
     * 如果对象不为空，则获取对象中的某个字段，否则返回默认值
     */
    @JvmStatic
    fun <T : Any, E> notNullGetter(obj: T?, func: ((T) -> E)?, defaultValue: E?): E? {
        if (obj != null && func != null) {
            return func(obj)
        }
        return defaultValue
    }

    /**
     * 如果值不为空，则返回值，否则返回默认值
     */
    @JvmStatic
    fun <T : Any> notNull(obj: T?, defaultValue: T?): T? {
        return obj ?: defaultValue
    }
}
