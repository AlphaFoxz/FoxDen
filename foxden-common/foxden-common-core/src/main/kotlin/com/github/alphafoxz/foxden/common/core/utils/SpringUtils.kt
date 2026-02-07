package com.github.alphafoxz.foxden.common.core.utils

import cn.hutool.extra.spring.SpringUtil
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.boot.autoconfigure.thread.Threading
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment

/**
 * Spring 工具类
 */
object SpringUtils {
    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     */
    @JvmStatic
    fun containsBean(name: String): Boolean {
        return getApplicationContext().containsBean(name)
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype
     */
    @JvmStatic
    @Throws(NoSuchBeanDefinitionException::class)
    fun isSingleton(name: String): Boolean {
        return getApplicationContext().isSingleton(name)
    }

    /**
     * 获取注册对象的类型
     */
    @JvmStatic
    @Throws(NoSuchBeanDefinitionException::class)
    fun getType(name: String): Class<*>? {
        return getApplicationContext().getType(name)
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     */
    @JvmStatic
    @Throws(NoSuchBeanDefinitionException::class)
    fun getAliases(name: String): Array<String> {
        return getApplicationContext().getAliases(name)
    }

    /**
     * 获取配置属性
     *
     * @param key 配置键
     * @return 配置值
     */
    @JvmStatic
    fun getProperty(key: String): String? {
        return getApplicationContext().getEnvironment().getProperty(key)
    }

    /**
     * 获取应用名称
     *
     * @return 应用名称
     */
    @JvmStatic
    fun getApplicationName(): String {
        return getApplicationContext().applicationName ?: "application"
    }

    /**
     * 获取aop代理对象
     */
    @JvmStatic
    fun <T> getAopProxy(invoker: T): T {
        @Suppress("UNCHECKED_CAST")
        return getBean(invoker!!.javaClass) as T
    }

    /**
     * 获取spring上下文
     */
    @JvmStatic
    fun context(): ApplicationContext {
        return getApplicationContext()
    }

    /**
     * 获取Bean工厂
     */
    @JvmStatic
    fun getBeanFactory(): org.springframework.beans.factory.config.ConfigurableListableBeanFactory {
        val context = getApplicationContext()
        if (context is org.springframework.context.ConfigurableApplicationContext) {
            return context.beanFactory
        }
        throw IllegalStateException("ApplicationContext is not ConfigurableApplicationContext")
    }

    /**
     * 获取Bean
     */
    @JvmStatic
    fun <T> getBean(clazz: Class<T>): T {
        return getApplicationContext().getBean(clazz)
    }

    /**
     * 根据名称获取Bean
     */
    @JvmStatic
    fun <T> getBean(name: String, clazz: Class<T>): T {
        return getApplicationContext().getBean(name, clazz)
    }

    /**
     * 根据名称获取Bean（使用reified类型参数）
     */
    @JvmStatic
    inline fun <reified T> getBean(name: String): T {
        return SpringUtil.getApplicationContext().getBean(name, T::class.java)
    }

    /**
     * 获取配置属性
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    @JvmStatic
    fun getProperty(key: String, defaultValue: String): String {
        return getApplicationContext().getEnvironment().getProperty(key, defaultValue)
    }

    /**
     * 判断是否使用虚拟线程
     */
    @JvmStatic
    fun isVirtual(): Boolean {
        return Threading.VIRTUAL.isActive(getBean(Environment::class.java))
    }

    private fun getApplicationContext(): ApplicationContext {
        return SpringUtil.getApplicationContext()
    }
}
