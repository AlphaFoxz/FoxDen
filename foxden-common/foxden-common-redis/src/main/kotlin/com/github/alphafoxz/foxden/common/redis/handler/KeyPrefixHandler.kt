package com.github.alphafoxz.foxden.common.redis.handler

import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import org.redisson.api.NameMapper

/**
 * redis缓存key前缀处理
 */
class KeyPrefixHandler(keyPrefix: String) : NameMapper {
    private val keyPrefix: String = if (StringUtils.isBlank(keyPrefix)) "" else "$keyPrefix:"

    /**
     * 增加前缀
     */
    override fun map(name: String?): String? {
        if (StringUtils.isBlank(name)) {
            return null
        }
        val nameValue = name!!
        return if (StringUtils.isNotBlank(keyPrefix) && !nameValue.startsWith(keyPrefix)) {
            keyPrefix + nameValue
        } else {
            nameValue
        }
    }

    /**
     * 去除前缀
     */
    override fun unmap(name: String?): String? {
        if (StringUtils.isBlank(name)) {
            return null
        }
        val nameValue = name!!
        return if (StringUtils.isNotBlank(keyPrefix) && nameValue.startsWith(keyPrefix)) {
            nameValue.substring(keyPrefix.length)
        } else {
            nameValue
        }
    }
}
