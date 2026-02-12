package com.github.alphafoxz.foxden.common.json.handler

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.NumberSerializer
import java.io.IOException

/**
 * BigNumber 序列化器
 * 对于 JS 安全范围内的 Long 值使用数字序列化，超出范围的才使用字符串
 * 避免前端精度丢失的同时，确保 total 等字段是数字类型
 */
class BigNumberSerializer : NumberSerializer(Number::class.java) {

    companion object {
        // JS Number.MAX_SAFE_INTEGER 与 Number.MIN_SAFE_INTEGER
        private const val MAX_SAFE_INTEGER = 9007199254740991L
        private const val MIN_SAFE_INTEGER = -9007199254740991L

        @JvmField
        val INSTANCE = BigNumberSerializer()
    }

    override fun serialize(value: Number?, gen: JsonGenerator, provider: SerializerProvider) {
        value?.let {
            // 对于超出安全范围的值，序列化为字符串
            if (it.toLong() > MAX_SAFE_INTEGER || it.toLong() < MIN_SAFE_INTEGER) {
                gen.writeString(it.toString())
            } else {
                // 否则使用父类的默认序列化（数字）
                super.serialize(it, gen, provider)
            }
        }
    }
}
