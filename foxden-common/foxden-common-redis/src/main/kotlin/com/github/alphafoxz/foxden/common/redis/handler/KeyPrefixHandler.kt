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
        return if (StringUtils.isNotBlank(keyPrefix) && !name!!.startsWith(keyPrefix)) {
            keyPrefix + name
        } else {
            name
        }
    }

    /**
     * 去除前缀
     */
    override fun unmap(name: String?): String? {
        if (StringUtils.isBlank(name)) {
            return null
        }
        return if (StringUtils.isNotBlank(keyPrefix) && name!!.startsWith(keyPrefix)) {
            name!!.substring(keyPrefix.length)
        } else {
            name
        }
    }
}
