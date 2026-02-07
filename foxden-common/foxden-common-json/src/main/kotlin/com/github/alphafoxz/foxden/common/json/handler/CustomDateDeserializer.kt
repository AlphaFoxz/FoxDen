package com.github.alphafoxz.foxden.common.json.handler

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.io.IOException
import java.util.Date

/**
 * 自定义日期反序列化器
 * 支持多种日期格式
 */
class CustomDateDeserializer : JsonDeserializer<Date>() {
    @Throws(IOException::class)
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Date {
        val text = p.text
        // 支持时间戳格式
        return try {
            text.toLongOrNull()?.let { Date(it) } ?: throw IOException("Invalid date format: $text")
        } catch (e: Exception) {
            throw IOException("Invalid date format: $text", e)
        }
    }
}
