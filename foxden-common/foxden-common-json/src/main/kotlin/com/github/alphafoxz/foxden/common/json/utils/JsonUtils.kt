package com.github.alphafoxz.foxden.common.json.utils

import cn.hutool.core.lang.Dict
import cn.hutool.core.util.ArrayUtil
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import java.io.IOException
import java.util.ArrayList

/**
 * JSON 工具类
 */
object JsonUtils {
    private val OBJECT_MAPPER: ObjectMapper by lazy { SpringUtils.getBean(ObjectMapper::class.java) }

    @JvmStatic
    fun getObjectMapper(): ObjectMapper = OBJECT_MAPPER

    /**
     * 将对象转换为JSON格式的字符串
     */
    @JvmStatic
    fun toJsonString(`object`: Any?): String? {
        if (`object` == null) return null
        return try {
            OBJECT_MAPPER.writeValueAsString(`object`)
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }

    /**
     * 将JSON格式的字符串转换为指定类型的对象
     */
    @JvmStatic
    fun <T> parseObject(text: String?, clazz: Class<T>): T? {
        if (StringUtils.isEmpty(text)) return null
        return try {
            OBJECT_MAPPER.readValue(text, clazz)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * 将字节数组转换为指定类型的对象
     */
    @JvmStatic
    fun <T> parseObject(bytes: ByteArray?, clazz: Class<T>): T? {
        if (ArrayUtil.isEmpty(bytes)) return null
        return try {
            OBJECT_MAPPER.readValue(bytes, clazz)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * 将JSON格式的字符串转换为指定类型的对象，支持复杂类型
     */
    @JvmStatic
    fun <T> parseObject(text: String?, typeReference: TypeReference<T>): T? {
        if (StringUtils.isBlank(text)) return null
        return try {
            OBJECT_MAPPER.readValue(text, typeReference)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * 将JSON格式的字符串转换为Dict对象
     */
    @JvmStatic
    fun parseMap(text: String?): Dict? {
        if (StringUtils.isBlank(text)) return null
        return try {
            OBJECT_MAPPER.readValue(text, OBJECT_MAPPER.typeFactory.constructType(Dict::class.java))
        } catch (e: MismatchedInputException) {
            // 类型不匹配说明不是json
            return null
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * 将JSON格式的字符串转换为Dict对象的列表
     */
    @JvmStatic
    fun parseArrayMap(text: String?): List<Dict>? {
        if (StringUtils.isBlank(text)) return null
        return try {
            OBJECT_MAPPER.readValue(
                text,
                OBJECT_MAPPER.typeFactory.constructCollectionType(List::class.java, Dict::class.java)
            )
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * 将JSON格式的字符串转换为指定类型对象的列表
     */
    @JvmStatic
    fun <T> parseArray(text: String?, clazz: Class<T>): List<T> {
        if (StringUtils.isEmpty(text)) return ArrayList()
        return try {
            OBJECT_MAPPER.readValue(
                text,
                OBJECT_MAPPER.typeFactory.constructCollectionType(List::class.java, clazz)
            )
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
