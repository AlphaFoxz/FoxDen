package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.web.core.BaseController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 缓存监控
 *
 * @author FoxDen Team
 */
@RestController
@RequestMapping("/monitor/cache")
class CacheController : BaseController() {

    /**
     * 获取缓存监控信息
     */
    @SaCheckPermission("monitor:cache:list")
    @GetMapping()
    fun getInfo(): R<Map<String, Any>> {
        // RedisUtils.getInfo() 方法不存在，返回空信息
        val info = mapOf<String, Any>(
            "redis_version" to "unknown",
            "used_memory" to "unknown",
            "used_memory_human" to "unknown"
        )
        return R.ok(info)
    }
}
