package com.github.alphafoxz.foxden.common.translation.core.handler

import cn.hutool.core.util.ObjectUtil
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.core.utils.reflect.ReflectUtils
import com.github.alphafoxz.foxden.common.translation.annotation.Translation
import com.github.alphafoxz.foxden.common.translation.core.TranslationInterface
import org.slf4j.LoggerFactory

/**
 * 翻译处理器
 *
 * @author Lion Li
 */
class TranslationHandler : JsonSerializer<Any?>(), ContextualSerializer {

    private val log = LoggerFactory.getLogger(TranslationHandler::class.java)

    private var translation: Translation? = null

    override fun serialize(value: Any?, gen: JsonGenerator, serializers: SerializerProvider) {
        val trans = TRANSLATION_MAPPER[translation?.type]
        if (ObjectUtil.isNotNull(trans)) {
            var transValue = value

            // 如果映射字段不为空 则取映射字段的值
            if (StringUtils.isNotBlank(translation?.mapper)) {
                transValue = ReflectUtils.invokeGetter(gen.currentValue, translation?.mapper ?: "")
            }

            // 如果为 null 直接写出
            if (ObjectUtil.isNull(transValue)) {
                gen.writeNull()
                return
            }

            try {
                val result = trans?.translation(transValue, translation?.other ?: "")
                gen.writeObject(result)
            } catch (e: Exception) {
                log.error("翻译处理异常，type: {}, value: {}", translation?.type, transValue, e)
                // 出现异常时输出原始值而不是中断序列化
                gen.writeObject(transValue)
            }
        } else {
            gen.writeObject(value)
        }
    }

    @Throws(JsonMappingException::class)
    override fun createContextual(prov: SerializerProvider, property: BeanProperty): JsonSerializer<*> {
        val translation = property.getAnnotation(Translation::class.java)
        if (translation != null) {
            this.translation = translation
            return this
        }
        return prov.findValueSerializer(property.type, property)
    }

    companion object {
        /**
         * 全局翻译实现类映射器
         */
        val TRANSLATION_MAPPER: MutableMap<String, TranslationInterface<*>> = mutableMapOf()
    }
}
