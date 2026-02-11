package com.github.alphafoxz.foxden.common.translation.config

import com.github.alphafoxz.foxden.common.translation.annotation.TranslationType
import com.github.alphafoxz.foxden.common.translation.core.TranslationInterface
import com.github.alphafoxz.foxden.common.translation.core.handler.TranslationHandler
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Configuration

/**
 * 翻译自动配置
 *
 * @author Lion Li
 */
@AutoConfiguration
class TranslationAutoConfiguration : BeanPostProcessor, ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        if (bean is TranslationInterface<*>) {
            val annotation = bean.javaClass.getAnnotation(TranslationType::class.java)
            if (annotation != null) {
                TranslationHandler.TRANSLATION_MAPPER[annotation.type] = bean
            }
        }
        return bean
    }
}
