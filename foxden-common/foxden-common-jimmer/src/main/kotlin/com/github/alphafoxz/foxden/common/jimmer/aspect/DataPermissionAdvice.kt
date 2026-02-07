package com.github.alphafoxz.foxden.common.jimmer.aspect

import com.github.alphafoxz.foxden.common.jimmer.annotation.DataPermission
import com.github.alphafoxz.foxden.common.jimmer.helper.DataPermissionHelper
import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * 数据权限注解Advice
 */
class DataPermissionAdvice : MethodInterceptor {
    private val log = LoggerFactory.getLogger(DataPermissionAdvice::class.java)

    override fun invoke(invocation: MethodInvocation): Any? {
        val target = invocation.`this` ?: return invocation.proceed()
        val method = invocation.method
        val args = invocation.arguments

        // 设置权限注解
        getDataPermissionAnnotation(target, method)?.let { DataPermissionHelper.setPermission(it) }

        return try {
            // 执行代理方法
            invocation.proceed()
        } finally {
            // 清除权限注解
            DataPermissionHelper.removePermission()
        }
    }

    /**
     * 获取数据权限注解
     */
    private fun getDataPermissionAnnotation(target: Any, method: Method): DataPermission? {
        // 优先获取方法上的注解
        method.getAnnotation(DataPermission::class.java)?.let { return it }

        // 方法上没有注解，则获取类上的注解
        var targetClass: Class<*> = target.javaClass
        // 如果是 JDK 动态代理，则获取真实的Class实例
        if (Proxy.isProxyClass(targetClass)) {
            targetClass = targetClass.interfaces[0]
        }
        return targetClass.getAnnotation(DataPermission::class.java)
    }
}
