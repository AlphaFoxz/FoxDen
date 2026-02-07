package com.github.alphafoxz.foxden.common.jimmer.helper

import cn.dev33.satoken.context.SaHolder
import cn.dev33.satoken.context.model.SaStorage
import cn.hutool.core.util.ObjectUtil
import com.github.alphafoxz.foxden.common.jimmer.annotation.DataPermission
import java.util.function.Supplier

/**
 * 数据权限助手
 */
object DataPermissionHelper {
    private const val DATA_PERMISSION_KEY = "data:permission"

    private val PERMISSION_CACHE = ThreadLocal<DataPermission>()

    /**
     * 获取当前执行mapper权限注解
     */
    fun getPermission(): DataPermission? {
        return PERMISSION_CACHE.get()
    }

    /**
     * 设置当前执行mapper权限注解
     */
    fun setPermission(dataPermission: DataPermission?) {
        if (dataPermission != null) {
            PERMISSION_CACHE.set(dataPermission)
        }
    }

    /**
     * 删除当前执行mapper权限注解
     */
    fun removePermission() {
        PERMISSION_CACHE.remove()
    }

    /**
     * 从上下文中获取指定键的变量值
     */
    fun <T> getVariable(key: String): T? {
        val context = getContext()
        @Suppress("UNCHECKED_CAST")
        return context[key] as? T
    }

    /**
     * 向上下文中设置指定键的变量值
     */
    fun setVariable(key: String, value: Any?) {
        val context = getContext()
        value?.let { context[key] = it }
    }

    /**
     * 获取数据权限上下文
     */
    fun getContext(): MutableMap<String, Any> {
        val saStorage: SaStorage = SaHolder.getStorage()
        var attribute = saStorage.get(DATA_PERMISSION_KEY)
        if (ObjectUtil.isNull(attribute)) {
            val newContext = HashMap<String, Any>()
            saStorage.set(DATA_PERMISSION_KEY, newContext)
            attribute = newContext
        }
        @Suppress("UNCHECKED_CAST")
        return attribute as? MutableMap<String, Any>
            ?: throw NullPointerException("data permission context type exception")
    }

    /**
     * 在忽略数据权限中执行
     */
    fun ignore(handle: Runnable) {
        try {
            handle.run()
        } finally {
            // Jimmer 的数据权限忽略机制需要单独实现
        }
    }

    /**
     * 在忽略数据权限中执行
     */
    fun <T> ignore(handle: Supplier<T>): T? {
        return try {
            handle.get()
        } finally {
            // Jimmer 的数据权限忽略机制需要单独实现
        }
    }
}
