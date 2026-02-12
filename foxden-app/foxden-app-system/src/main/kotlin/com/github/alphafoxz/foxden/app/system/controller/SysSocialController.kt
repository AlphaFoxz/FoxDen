package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.service.SysSocialService
import com.github.alphafoxz.foxden.domain.system.vo.SysSocialVo
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 社交关系 控制层
 *
 * @author FoxDen Team
 */
@Validated
@RestController
@RequestMapping("/system/social")
class SysSocialController(
    private val socialService: SysSocialService
) : BaseController() {

    /**
     * 查询社交关系列表
     */
    @SaCheckPermission("system:social:list")
    @GetMapping("/list")
    fun list(): R<List<SysSocialVo>> {
        return R.ok(socialService.selectSocialListByUserId(LoginHelper.getUserId()!!))
    }

}
