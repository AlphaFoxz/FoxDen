package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.dev33.satoken.annotation.SaCheckRole
import com.github.alphafoxz.foxden.common.core.constant.TenantConstants
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.excel.utils.ExcelUtil
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.jimmer.helper.TenantHelper
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysTenantBo
import com.github.alphafoxz.foxden.domain.system.service.SysTenantService
import com.github.alphafoxz.foxden.domain.system.service.extensions.queryPageList
import com.github.alphafoxz.foxden.domain.system.vo.SysTenantVo
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
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
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:list")
    @GetMapping("/list")
    fun list(bo: SysTenantBo, pageQuery: PageQuery): TableDataInfo<SysTenantVo> {
        return tenantService.queryPageList(bo, pageQuery)
    }

    /**
     * 导出租户列表
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
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
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:query")
    @GetMapping("/{id}")
    fun getInfo(@NotNull(message = "主键不能为空") @PathVariable id: Long): R<SysTenantVo> {
        return R.ok(tenantService.queryById(id))
    }

    /**
     * 新增租户
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:add")
    @Log(title = "租户管理", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(@Validated @RequestBody bo: SysTenantBo): R<Void> {
        if (!tenantService.checkTenantNameUnique(bo)) {
            return R.fail("新增租户'" + bo.companyName + "'失败，企业名称已存在")
        }
        return toAjax(TenantHelper.ignore {
            tenantService.insertTenant(bo)
        })
    }

    /**
     * 修改租户
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:edit")
    @Log(title = "租户管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(@Validated @RequestBody bo: SysTenantBo): R<Void> {
        tenantService.checkTenantAllowed(bo.tenantId!!)
        if (!tenantService.checkTenantNameUnique(bo)) {
            return R.fail("修改租户'" + bo.companyName + "'失败，公司名称已存在")
        }
        return toAjax(tenantService.updateTenant(bo))
    }

    /**
     * 状态修改
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:edit")
    @Log(title = "租户管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/changeStatus")
    fun changeStatus(@RequestBody bo: SysTenantBo): R<Void> {
        tenantService.checkTenantAllowed(bo.tenantId!!)
        return toAjax(tenantService.updateTenantStatus(bo.tenantId!!, bo.status!!))
    }

    /**
     * 删除租户
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:remove")
    @Log(title = "租户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    fun remove(@NotEmpty(message = "主键不能为空") @PathVariable ids: Array<Long>): R<Void> {
        return toAjax(tenantService.deleteWithValidByIds(ids.toList(), true))
    }

    /**
     * 动态切换租户
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @GetMapping("/dynamic/{tenantId}")
    fun dynamicTenant(@NotBlank(message = "租户ID不能为空") @PathVariable tenantId: String): R<Void> {
        tenantService.dynamicTenant(tenantId)
        return R.ok()
    }

    /**
     * 清除动态租户
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @GetMapping("/dynamic/clear")
    fun dynamicClear(): R<Void> {
        tenantService.clearDynamic()
        return R.ok()
    }

    /**
     * 同步租户套餐
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:edit")
    @Log(title = "租户管理", businessType = BusinessType.UPDATE)
    @GetMapping("/syncTenantPackage")
    fun syncTenantPackage(
        @NotBlank(message = "租户ID不能为空") @RequestParam tenantId: String,
        @NotNull(message = "套餐ID不能为空") @RequestParam packageId: Long
    ): R<Void> {
        return toAjax(TenantHelper.ignore {
            tenantService.syncTenantPackage(tenantId, packageId)
        })
    }

    /**
     * 同步租户字典
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @Log(title = "租户管理", businessType = BusinessType.INSERT)
    @GetMapping("/syncTenantDict")
    fun syncTenantDict(): R<String> {
        if (!TenantHelper.isEnable()) {
            return R.fail("当前未开启租户模式")
        }
        tenantService.syncTenantDict()
        return R.ok("同步租户字典成功")
    }

    /**
     * 同步租户参数配置
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @Log(title = "租户管理", businessType = BusinessType.INSERT)
    @GetMapping("/syncTenantConfig")
    fun syncTenantConfig(): R<String> {
        if (!TenantHelper.isEnable()) {
            return R.fail("当前未开启租户模式")
        }
        tenantService.syncTenantConfig()
        return R.ok("同步租户参数配置成功")
    }
}
