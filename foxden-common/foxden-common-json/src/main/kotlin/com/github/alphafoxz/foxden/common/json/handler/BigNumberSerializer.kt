package com.github.alphafoxz.foxden.common.json.handler

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException

/**
 * BigNumber 序列化器
 * 将 Long 类型转换为 String，避免前端精度丢失
 */
class BigNumberSerializer : JsonSerializer<Any>() {
    override fun serialize(value: Any, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.toString())
    }

    companion object {
        @JvmField
        val INSTANCE = BigNumberSerializer()
    }
}
