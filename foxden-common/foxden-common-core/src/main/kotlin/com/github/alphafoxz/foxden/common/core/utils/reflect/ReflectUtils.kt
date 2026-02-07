package com.github.alphafoxz.foxden.common.core.utils.reflect

import cn.hutool.core.util.ReflectUtil
import cn.hutool.core.util.StrUtil
import java.lang.reflect.Method

/**
 * 反射工具类
 */
@Suppress("UNCHECKED_CAST", "UNUSED")
object ReflectUtils {
    private const val SETTER_PREFIX = "set"
    private const val GETTER_PREFIX = "get"

    /**
     * 调用Getter方法
     * 支持多级，如：对象名.对象名.方法
     */
    @JvmStatic
    fun <E> invokeGetter(obj: Any?, propertyName: String): E? {
        var `object`: Any? = obj
        val names = StrUtil.split(propertyName, ".")
        for (name in names) {
            val getterMethodName = GETTER_PREFIX + StrUtil.upperFirst(name)
            `object` = ReflectUtil.invoke(`object`, getterMethodName)
        }
        return `object` as? E
    }

    /**
     * 调用Setter方法, 仅匹配方法名
     * 支持多级，如：对象名.对象名.方法
     */
    @JvmStatic
    fun <E> invokeSetter(obj: Any?, propertyName: String, value: E) {
        var `object`: Any? = obj
        val names = StrUtil.split(propertyName, ".")
        for (i in names.indices) {
            if (i < names.size - 1) {
                val getterMethodName = GETTER_PREFIX + StrUtil.upperFirst(names[i])
                `object` = ReflectUtil.invoke(`object`, getterMethodName)
            } else {
                val setterMethodName = SETTER_PREFIX + StrUtil.upperFirst(names[i])
                val method = ReflectUtil.getMethodByName(`object`!!.javaClass, setterMethodName)
                ReflectUtil.invoke(`object`, method, value)
            }
        }
    }

    /**
     * 创建实例
     *
     * @param clazz 类
     * @return 对象
     */
    @JvmStatic
    fun <T> newInstance(clazz: Class<T>): T {
        return clazz.getDeclaredConstructor().newInstance()
    }

    /**
     * 创建实例（带参数）
     *
     * @param clazz 类
     * @param parameters 参数
     * @return 对象
     */
    @JvmStatic
    fun <T> newInstance(clazz: Class<T>, vararg parameters: Any?): T {
        val constructor = if (parameters.isEmpty()) {
            clazz.getDeclaredConstructor()
        } else {
            val parameterTypes = parameters.map { it?.javaClass }.toTypedArray()
            clazz.getDeclaredConstructor(*parameterTypes)
        }
        constructor.isAccessible = true
        return constructor.newInstance(*parameters)
    }
}
