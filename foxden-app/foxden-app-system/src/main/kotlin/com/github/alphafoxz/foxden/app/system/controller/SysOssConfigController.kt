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
import com.github.alphafoxz.foxden.domain.system.bo.SysOssConfigBo
import com.github.alphafoxz.foxden.domain.system.service.SysOssConfigService
import com.github.alphafoxz.foxden.domain.system.vo.SysOssConfigVo
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 对象存储配置
 *
 * @author FoxDen Team
 */
@Validated
@RestController
@RequestMapping("/resource/oss/config")
class SysOssConfigController(
    private val ossConfigService: SysOssConfigService
) : BaseController() {

    /**
     * 查询对象存储配置列表
     */
    @SaCheckPermission("system:ossConfig:list")
    @GetMapping("/list")
    fun list(bo: SysOssConfigBo, pageQuery: PageQuery): TableDataInfo<SysOssConfigVo> {
        val list = ossConfigService.selectOssConfigList(bo)
        return TableDataInfo.build(list)
    }

    /**
     * 获取对象存储配置详细信息
     *
     * @param ossConfigId OSS配置ID
     */
    @SaCheckPermission("system:ossConfig:list")
    @GetMapping("/{ossConfigId}")
    fun getInfo(@NotNull(message = "主键不能为空") @PathVariable ossConfigId: Long): R<SysOssConfigVo> {
        return R.ok(ossConfigService.selectOssConfigById(ossConfigId))
    }

    /**
     * 新增对象存储配置
     */
    @SaCheckPermission("system:ossConfig:add")
    @Log(title = "对象存储配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    fun add(@Validated(AddGroup::class) @RequestBody bo: SysOssConfigBo): R<Void> {
        return toAjax(ossConfigService.insertOssConfig(bo))
    }

    /**
     * 修改对象存储配置
     */
    @SaCheckPermission("system:ossConfig:edit")
    @Log(title = "对象存储配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    fun edit(@Validated(EditGroup::class) @RequestBody bo: SysOssConfigBo): R<Void> {
        return toAjax(ossConfigService.updateOssConfig(bo))
    }

    /**
     * 删除对象存储配置
     *
     * @param ossConfigIds OSS配置ID串
     */
    @SaCheckPermission("system:ossConfig:remove")
    @Log(title = "对象存储配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ossConfigIds}")
    fun remove(@NotEmpty(message = "主键不能为空") @PathVariable ossConfigIds: Array<Long>): R<Void> {
        ossConfigService.deleteOssConfigByIds(ossConfigIds)
        return toAjax(ossConfigIds.size)
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:ossConfig:edit")
    @Log(title = "对象存储状态修改", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/changeStatus")
    fun changeStatus(@RequestBody bo: SysOssConfigBo): R<Void> {
        // TODO: Implement status change when service method is available
        return R.fail("状态修改功能待实现")
    }
}
