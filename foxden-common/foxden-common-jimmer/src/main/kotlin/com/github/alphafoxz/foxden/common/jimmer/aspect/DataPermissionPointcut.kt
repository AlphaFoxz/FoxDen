package com.github.alphafoxz.foxden.common.jimmer.aspect

import com.github.alphafoxz.foxden.common.jimmer.annotation.DataPermission
import org.slf4j.LoggerFactory
import org.springframework.aop.support.StaticMethodMatcherPointcut
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * 数据权限匹配切点
 */
class DataPermissionPointcut : StaticMethodMatcherPointcut() {
    private val log = LoggerFactory.getLogger(DataPermissionPointcut::class.java)

    override fun matches(method: Method, targetClass: Class<*>): Boolean {
        // 优先匹配方法
        if (method.isAnnotationPresent(DataPermission::class.java)) {
            return true
        }

        // 检查是否是 JDK 动态代理
        var targetClassRef: Class<*> = targetClass
        if (Proxy.isProxyClass(targetClassRef)) {
            targetClassRef = targetClassRef.interfaces[0]
        }
        return targetClassRef.isAnnotationPresent(DataPermission::class.java)
    }
}
