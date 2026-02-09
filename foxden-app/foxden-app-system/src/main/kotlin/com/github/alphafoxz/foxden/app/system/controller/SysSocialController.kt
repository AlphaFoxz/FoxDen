package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.validate.AddGroup
import com.github.alphafoxz.foxden.common.core.validate.EditGroup
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysSocialBo
import com.github.alphafoxz.foxden.domain.system.service.SysSocialService
import com.github.alphafoxz.foxden.domain.system.vo.SysSocialVo
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

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
    fun list(bo: SysSocialBo, pageQuery: PageQuery): TableDataInfo<SysSocialVo> {
        val list = socialService.selectSocialList(bo)
        return TableDataInfo.build(list)
    }

    /**
     * 获取社交关系详细信息
     *
     * @param socialId 社交关系ID
     */
    @SaCheckPermission("system:social:query")
    @GetMapping("/{socialId}")
    fun getInfo(@PathVariable socialId: Long): R<SysSocialVo> {
        return R.ok(socialService.selectSocialById(socialId))
    }

    /**
     * 新增社交关系
     */
    @SaCheckPermission("system:social:add")
    @Log(title = "社交关系", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    fun add(@Validated(AddGroup::class) @RequestBody bo: SysSocialBo): R<Void> {
        return toAjax(socialService.insertSocial(bo))
    }

    /**
     * 修改社交关系
     */
    @SaCheckPermission("system:social:edit")
    @Log(title = "社交关系", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    fun edit(@Validated(EditGroup::class) @RequestBody bo: SysSocialBo): R<Void> {
        return toAjax(socialService.updateSocial(bo))
    }

    /**
     * 删除社交关系
     *
     * @param socialIds 社交关系ID串
     */
    @SaCheckPermission("system:social:remove")
    @Log(title = "社交关系", businessType = BusinessType.DELETE)
    @DeleteMapping("/{socialIds}")
    fun remove(@NotEmpty(message = "主键不能为空") @PathVariable socialIds: Array<Long>): R<Void> {
        for (socialId in socialIds) {
            socialService.deleteSocialById(socialId)
        }
        return toAjax(socialIds.size)
    }
}
