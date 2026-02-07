package com.github.alphafoxz.foxden.common.jimmer.aspect

import org.aopalliance.aop.Advice
import org.springframework.aop.Pointcut
import org.springframework.aop.support.AbstractPointcutAdvisor

/**
 * 数据权限注解切面定义
 */
class DataPermissionPointcutAdvisor : AbstractPointcutAdvisor() {
    private val advice: Advice = DataPermissionAdvice()
    private val pointcut: Pointcut = DataPermissionPointcut()

    override fun getPointcut(): Pointcut {
        return pointcut
    }

    override fun getAdvice(): Advice {
        return advice
    }
}
