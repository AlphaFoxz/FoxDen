package com.github.alphafoxz.foxden.common.tenant.handler

import com.github.alphafoxz.foxden.common.core.constant.GlobalConstants
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.jimmer.helper.TenantHelper
import com.github.alphafoxz.foxden.common.redis.handler.KeyPrefixHandler

/**
 * 多租户redis缓存key前缀处理
 *
 * @author Lion Li
 */
class TenantKeyPrefixHandler(keyPrefix: String) : KeyPrefixHandler(keyPrefix) {

    /**
     * 增加前缀
     * 格式: tenantId:keyPrefix:name
     */
    override fun map(name: String?): String? {
        if (StringUtils.isBlank(name)) {
            return null
        }

        val nameValue = name!!

        // 全局缓存 key 不加租户前缀
        if (StringUtils.contains(nameValue, GlobalConstants.GLOBAL_REDIS_KEY)) {
            return super.map(nameValue)
        }

        // 如果已经以租户ID开头，直接返回
        val tenantId = TenantHelper.getTenantId()
        if (StringUtils.isNotBlank(tenantId) && nameValue.startsWith("${tenantId}:")) {
            return super.map(nameValue)
        }

        // 添加租户前缀
        return if (StringUtils.isNotBlank(tenantId)) {
            super.map("${tenantId}:${nameValue}")
        } else {
            super.map(nameValue)
        }
    }

    /**
     * 去除前缀
     */
    override fun unmap(name: String?): String? {
        val unmap = super.unmap(name)
        if (StringUtils.isBlank(unmap)) {
            return null
        }

        val nameValue = unmap!!

        // 全局缓存 key 不处理
        if (StringUtils.contains(name, GlobalConstants.GLOBAL_REDIS_KEY)) {
            return nameValue
        }

        // 移除租户前缀
        val tenantId = TenantHelper.getTenantId()
        return if (StringUtils.isNotBlank(tenantId) && nameValue.startsWith("${tenantId}:")) {
            nameValue.substring((tenantId + ":").length)
        } else {
            nameValue
        }
    }
}
