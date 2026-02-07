package com.github.alphafoxz.foxden.app.admin.controller

import cn.dev33.satoken.annotation.SaIgnore
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 首页
 *
 * @author FoxDen Team
 */
@SaIgnore
@RestController
class IndexController {

    /**
     * 访问首页，提示语
     */
    @GetMapping("/")
    fun index(): String {
        return StringUtils.format("欢迎使用{}后台管理框架，请通过前端地址访问。", SpringUtils.getApplicationName()) ?: "欢迎使用后台管理框架，请通过前端地址访问。"
    }
}
