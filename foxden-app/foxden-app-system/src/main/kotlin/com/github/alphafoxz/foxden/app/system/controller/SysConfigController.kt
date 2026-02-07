package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysConfigBo
import com.github.alphafoxz.foxden.domain.system.service.SysConfigService
import com.github.alphafoxz.foxden.domain.system.service.extensions.selectConfigById
import com.github.alphafoxz.foxden.domain.system.vo.SysConfigVo
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 参数配置 信息操作处理
 *
 * @author FoxDen Team
 */
@Validated
@RestController
@RequestMapping("/system/config")
class SysConfigController(
    private val configService: SysConfigService
) : BaseController() {

    /**
     * 获取参数配置列表
     */
    @SaCheckPermission("system:config:list")
    @GetMapping("/list")
    fun list(config: SysConfigBo): R<List<SysConfigVo>> {
        return R.ok(configService.selectConfigList(config))
    }

    /**
     * 根据参数编号获取详细信息
     */
    @SaCheckPermission("system:config:query")
    @GetMapping("/{configId}")
    fun getInfo(@PathVariable configId: Long): R<SysConfigVo> {
        return R.ok(configService.selectConfigById(configId))
    }

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping("/configKey/{configKey}")
    fun getConfigKey(@PathVariable configKey: String): R<String> {
        return R.ok(configService.selectConfigByKey(configKey))
    }

    /**
     * 新增参数配置
     */
    @SaCheckPermission("system:config:add")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(@Validated @RequestBody config: SysConfigBo): R<Void> {
        if (!configService.checkConfigKeyUnique(config)) {
            return R.fail("新增参数'" + config.configName + "'失败，参数键名已存在")
        }
        return toAjax(configService.insertConfig(config))
    }

    /**
     * 修改参数配置
     */
    @SaCheckPermission("system:config:edit")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(@Validated @RequestBody config: SysConfigBo): R<Void> {
        if (!configService.checkConfigKeyUnique(config)) {
            return R.fail("修改参数'" + config.configName + "'失败，参数键名已存在")
        }
        return toAjax(configService.updateConfig(config))
    }

    /**
     * 删除参数配置
     */
    @SaCheckPermission("system:config:remove")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    fun remove(@PathVariable configIds: Array<Long>): R<Void> {
        configService.deleteConfigByIds(configIds)
        return R.ok()
    }

    /**
     * 刷新参数缓存
     */
    @SaCheckPermission("system:config:remove")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    fun refreshCache(): R<Void> {
        configService.resetConfigCache()
        return R.ok()
    }
}
