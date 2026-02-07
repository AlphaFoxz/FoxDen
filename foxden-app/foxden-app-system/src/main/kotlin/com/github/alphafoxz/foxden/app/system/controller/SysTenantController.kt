package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.excel.utils.ExcelUtil
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysTenantBo
import com.github.alphafoxz.foxden.domain.system.service.SysTenantService
import com.github.alphafoxz.foxden.domain.system.service.extensions.deleteWithValidByIds
import com.github.alphafoxz.foxden.domain.system.service.extensions.insertByBo
import com.github.alphafoxz.foxden.domain.system.service.extensions.queryList
import com.github.alphafoxz.foxden.domain.system.service.extensions.queryPageList
import com.github.alphafoxz.foxden.domain.system.service.extensions.updateByBo
import com.github.alphafoxz.foxden.domain.system.vo.SysTenantVo
import jakarta.servlet.http.HttpServletResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 租户管理
 *
 * @author FoxDen Team
 */
@Validated
@RestController
@RequestMapping("/system/tenant")
class SysTenantController(
    private val tenantService: SysTenantService
) : BaseController() {

    /**
     * 查询租户列表
     */
    @SaCheckPermission("system:tenant:list")
    @GetMapping("/list")
    fun list(bo: SysTenantBo, pageQuery: PageQuery): TableDataInfo<SysTenantVo> {
        val list = tenantService.selectTenantList(bo)
        val pageNum = pageQuery.pageNum ?: 1
        val pageSize = pageQuery.pageSize ?: list.size
        return TableDataInfo.build(list, pageNum, pageSize)
    }

    /**
     * 导出租户列表
     */
    @Log(title = "租户管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:tenant:export")
    @PostMapping("/export")
    fun export(bo: SysTenantBo, pageQuery: PageQuery, response: HttpServletResponse) {
        val list = tenantService.selectTenantList(bo)
        ExcelUtil.exportExcel(list, "租户数据", SysTenantVo::class.java, response)
    }

    /**
     * 获取租户详细信息
     */
    @SaCheckPermission("system:tenant:query")
    @GetMapping("/{tenantId}")
    fun getInfo(@PathVariable tenantId: String): R<SysTenantVo> {
        return R.ok(tenantService.queryByTenantId(tenantId))
    }

    /**
     * 新增租户
     */
    @SaCheckPermission("system:tenant:add")
    @Log(title = "租户管理", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(@Validated @RequestBody bo: SysTenantBo): R<Void> {
        if (!tenantService.checkTenantNameUnique(bo)) {
            return R.fail("新增租户'" + bo.companyName + "'失败，租户名称已存在")
        }
        return toAjax(tenantService.insertTenant(bo))
    }

    /**
     * 修改租户
     */
    @SaCheckPermission("system:tenant:edit")
    @Log(title = "租户管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(@Validated @RequestBody bo: SysTenantBo): R<Void> {
        tenantService.checkTenantAllowed(bo.tenantId!!)
        if (!tenantService.checkTenantNameUnique(bo)) {
            return R.fail("修改租户'" + bo.companyName + "'失败，租户名称已存在")
        }
        return toAjax(tenantService.updateTenant(bo))
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:tenant:edit")
    @Log(title = "租户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    fun changeStatus(@RequestBody bo: SysTenantBo): R<Void> {
        tenantService.checkTenantAllowed(bo.tenantId!!)
        tenantService.updateTenantStatus(bo.tenantId!!, bo.status!!)
        return R.ok()
    }

    /**
     * 删除租户
     */
    @SaCheckPermission("system:tenant:remove")
    @Log(title = "租户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tenantIds}")
    fun remove(@PathVariable tenantIds: Array<String>): R<Void> {
        tenantIds.forEach { tenantId ->
            tenantService.deleteTenantById(tenantId)
        }
        return R.ok()
    }
}
