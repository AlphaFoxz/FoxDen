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
import com.github.alphafoxz.foxden.domain.system.bo.SysClientBo
import com.github.alphafoxz.foxden.domain.system.service.SysClientService
import com.github.alphafoxz.foxden.domain.system.vo.SysClientVo
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 客户端管理 控制层
 *
 * @author FoxDen Team
 */
@Validated
@RestController
@RequestMapping("/system/client")
class SysClientController(
    private val clientService: SysClientService
) : BaseController() {

    /**
     * 查询客户端管理列表
     */
    @SaCheckPermission("system:client:list")
    @GetMapping("/list")
    fun list(bo: SysClientBo, pageQuery: PageQuery): TableDataInfo<SysClientVo> {
        val list = clientService.selectClientList(bo)
        return TableDataInfo.build(list)
    }

    /**
     * 获取客户端管理详细信息
     *
     * @param id 客户端管理主键
     */
    @SaCheckPermission("system:client:query")
    @GetMapping("/{id}")
    fun getInfo(@NotNull(message = "主键不能为空") @PathVariable id: Long): R<SysClientVo> {
        return R.ok(clientService.selectClientById(id))
    }

    /**
     * 根据客户端ID查询详情
     *
     * @param clientId 客户端ID
     */
    @SaCheckPermission("system:client:query")
    @GetMapping("/clientId/{clientId}")
    fun getInfoByClientId(@PathVariable clientId: String): R<SysClientVo> {
        return R.ok(clientService.queryByClientId(clientId))
    }

    /**
     * 新增客户端管理
     */
    @SaCheckPermission("system:client:add")
    @Log(title = "客户端管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    fun add(@Validated(AddGroup::class) @RequestBody bo: SysClientBo): R<Void> {
        if (!clientService.checkClientIdUnique(bo)) {
            return R.fail("新增客户端'" + (bo.clientName ?: "") + "'失败，客户端ID已存在")
        }
        return toAjax(clientService.insertClient(bo))
    }

    /**
     * 修改客户端管理
     */
    @SaCheckPermission("system:client:edit")
    @Log(title = "客户端管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    fun edit(@Validated(EditGroup::class) @RequestBody bo: SysClientBo): R<Void> {
        if (!clientService.checkClientIdUnique(bo)) {
            return R.fail("修改客户端'" + (bo.clientName ?: "") + "'失败，客户端ID已存在")
        }
        return toAjax(clientService.updateClient(bo))
    }

    /**
     * 删除客户端管理
     *
     * @param ids 客户端管理主键串
     */
    @SaCheckPermission("system:client:remove")
    @Log(title = "客户端管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    fun remove(@NotEmpty(message = "主键不能为空") @PathVariable ids: Array<Long>): R<Void> {
        for (id in ids) {
            clientService.deleteClientById(id)
        }
        return toAjax(ids.size)
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:client:edit")
    @Log(title = "客户端状态修改", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/changeStatus")
    fun changeStatus(@RequestBody bo: SysClientBo): R<Void> {
        // TODO: Implement status change when service method is available
        return toAjax(clientService.updateClient(bo))
    }
}
