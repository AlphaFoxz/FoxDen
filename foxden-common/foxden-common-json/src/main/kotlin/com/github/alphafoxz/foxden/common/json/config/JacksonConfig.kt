package com.github.alphafoxz.foxden.common.json.config

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.github.alphafoxz.foxden.common.json.handler.BigNumberSerializer
import com.github.alphafoxz.foxden.common.json.handler.CustomDateDeserializer
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.context.annotation.Bean
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.TimeZone

/**
 * Jackson 配置
 */
@AutoConfiguration(before = [JacksonAutoConfiguration::class])
class JacksonConfig {
    @Bean
    fun registerJavaTimeModule(): Module {
        // 全局配置序列化返回 JSON 处理
        val javaTimeModule = JavaTimeModule()

        // Long 序列化器 - 同时注册 Kotlin 和 Java 类型
        javaTimeModule.addSerializer(Long::class.java, BigNumberSerializer.INSTANCE)
        javaTimeModule.addSerializer(java.lang.Long::class.java, BigNumberSerializer.INSTANCE)
        javaTimeModule.addSerializer(java.lang.Long.TYPE, BigNumberSerializer.INSTANCE)

        // BigInteger 序列化器 - 同时注册 Kotlin 和 Java 类型
        javaTimeModule.addSerializer(BigInteger::class.java, BigNumberSerializer.INSTANCE)
        javaTimeModule.addSerializer(java.math.BigInteger::class.java, BigNumberSerializer.INSTANCE)

        // BigDecimal 序列化器 - 同时注册 Kotlin 和 Java 类型
        javaTimeModule.addSerializer(BigDecimal::class.java, ToStringSerializer.instance)
        javaTimeModule.addSerializer(java.math.BigDecimal::class.java, ToStringSerializer.instance)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        // LocalDateTime 序列化器 - 同时注册 Kotlin 和 Java 类型
        javaTimeModule.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(formatter))
        javaTimeModule.addSerializer(java.time.LocalDateTime::class.java, LocalDateTimeSerializer(formatter))
        javaTimeModule.addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(formatter))
        javaTimeModule.addDeserializer(java.time.LocalDateTime::class.java, LocalDateTimeDeserializer(formatter))

        // Date 反序列化器 - 同时注册 Kotlin 和 Java 类型
        javaTimeModule.addDeserializer(Date::class.java, CustomDateDeserializer())
        javaTimeModule.addDeserializer(java.util.Date::class.java, CustomDateDeserializer())

        return javaTimeModule
    }

    @Bean
    fun customizer(): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { builder ->
            builder.timeZone(TimeZone.getDefault())
        }
    }
}
