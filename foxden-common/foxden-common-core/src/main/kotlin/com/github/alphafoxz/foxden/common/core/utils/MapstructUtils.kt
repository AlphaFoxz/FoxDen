package com.github.alphafoxz.foxden.common.core.utils

import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.collection.CollUtil
import java.util.*

/**
 * Mapstruct 工具类
 *
 * @author FoxDen Team
 */
object MapstructUtils {

    /**
     * 转换为目标对象
     *
     * @param source 源对象
     * @param clazz 目标类
     * @return 目标对象
     */
    @JvmStatic
    fun <T> convert(source: Any?, clazz: Class<T>): T? {
        if (source == null) {
            return null
        }
        return BeanUtil.copyProperties(source, clazz)
    }

    /**
     * 转换为目标对象列表
     *
     * @param sources 源对象列表
     * @param clazz 目标类
     * @return 目标对象列表
     */
    @JvmStatic
    fun <T> convert(sources: Collection<Any>?, clazz: Class<T>): List<T> {
        if (CollUtil.isEmpty(sources)) {
            return ArrayList()
        }
        return sources!!.map { convert(it, clazz)!! }.filterNotNull()
    }

    /**
     * 转换为目标对象列表
     *
     * @param sources 源对象列表
     * @param clazz 目标类
     * @return 目标对象列表
     */
    @JvmStatic
    fun <T> convert(sources: List<Any?>?, clazz: Class<T>): List<T> {
        if (CollUtil.isEmpty(sources)) {
            return ArrayList()
        }
        return sources!!.mapNotNull { convert(it, clazz) }
    }
}
