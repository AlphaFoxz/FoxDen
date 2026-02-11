package com.github.alphafoxz.foxden.common.sensitive.handler

import cn.hutool.core.util.ObjectUtil
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.sensitive.annotation.Sensitive
import com.github.alphafoxz.foxden.common.sensitive.core.SensitiveService
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException

/**
 * 数据脱敏json序列化工具
 *
 * @author Yjoioooo
 */
class SensitiveHandler : JsonSerializer<String>(), ContextualSerializer {

    private val log = LoggerFactory.getLogger(SensitiveHandler::class.java)

    private var strategy: com.github.alphafoxz.foxden.common.sensitive.core.SensitiveStrategy? = null
    private var roleKey: Array<out String> = emptyArray()
    private var perms: Array<out String> = emptyArray()

    override fun serialize(value: String?, gen: JsonGenerator, serializers: SerializerProvider) {
        try {
            val sensitiveService = SpringUtils.getBean(SensitiveService::class.java)
            if (ObjectUtil.isNotNull(sensitiveService) && sensitiveService.isSensitive(roleKey as Array<String>, perms as Array<String>)) {
                if (value != null) {
                    gen.writeString(strategy?.desensitizer()?.apply(value) ?: value)
                } else {
                    gen.writeNull()
                }
            } else {
                gen.writeString(value)
            }
        } catch (e: BeansException) {
            log.error("脱敏实现不存在, 采用默认处理 => {}", e.message)
            gen.writeString(value)
        }
    }

    @Throws(JsonMappingException::class)
    override fun createContextual(prov: SerializerProvider, property: BeanProperty): JsonSerializer<*> {
        val annotation = property.getAnnotation(Sensitive::class.java)
        if (annotation != null && String::class.java == property.type.rawClass) {
            this.strategy = annotation.strategy
            this.roleKey = annotation.roleKey
            this.perms = annotation.perms
            return this
        }
        return prov.findValueSerializer(property.type, property)
    }
}
