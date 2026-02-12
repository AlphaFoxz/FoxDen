package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.dev33.satoken.annotation.SaCheckRole
import com.github.alphafoxz.foxden.common.core.constant.TenantConstants
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.validate.AddGroup
import com.github.alphafoxz.foxden.common.core.validate.EditGroup
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysTenantPackageBo
import com.github.alphafoxz.foxden.domain.system.service.SysTenantPackageService
import com.github.alphafoxz.foxden.domain.system.vo.SysTenantPackageVo
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 租户套餐管理
 *
 * @author FoxDen Team
 */
@Validated
@RestController
@RequestMapping("/system/tenant/package")
class SysTenantPackageController(
    private val tenantPackageService: SysTenantPackageService
) : BaseController() {

    /**
     * 查询租户套餐列表
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:list")
    @GetMapping("/list")
    fun list(bo: SysTenantPackageBo, pageQuery: PageQuery): TableDataInfo<SysTenantPackageVo> {
        return tenantPackageService.selectTenantPackageList(bo, pageQuery)
    }

    /**
     * 查询租户套餐下拉选列表
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:list")
    @GetMapping("/selectList")
    fun selectList(): R<List<SysTenantPackageVo>> {
        return R.ok(tenantPackageService.selectList())
    }

    /**
     * 获取租户套餐详细信息
     *
     * @param packageId 主键
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:query")
    @GetMapping("/{packageId}")
    fun getInfo(@NotNull(message = "主键不能为空") @PathVariable packageId: Long): R<SysTenantPackageVo> {
        return R.ok(tenantPackageService.selectTenantPackageById(packageId))
    }

    /**
     * 新增租户套餐
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:add")
    @Log(title = "租户套餐", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    fun add(@Validated(AddGroup::class) @RequestBody bo: SysTenantPackageBo): R<Void> {
        if (!tenantPackageService.checkPackageNameUnique(bo)) {
            return R.fail("新增套餐'${bo.packageName ?: ""}'失败，套餐名称已存在")
        }
        return toAjax(tenantPackageService.insertTenantPackage(bo))
    }

    /**
     * 修改租户套餐
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:edit")
    @Log(title = "租户套餐", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    fun edit(@Validated(EditGroup::class) @RequestBody bo: SysTenantPackageBo): R<Void> {
        if (!tenantPackageService.checkPackageNameUnique(bo)) {
            return R.fail("修改套餐'" + (bo.packageName ?: "") + "'失败，套餐名称已存在")
        }
        return toAjax(tenantPackageService.updateTenantPackage(bo))
    }

    /**
     * 删除租户套餐
     *
     * @param packageIds 主键
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:remove")
    @Log(title = "租户套餐", businessType = BusinessType.DELETE)
    @DeleteMapping("/{packageIds}")
    fun remove(@NotEmpty(message = "主键不能为空") @PathVariable packageIds: Array<Long>): R<Void> {
        tenantPackageService.deleteTenantPackageByIds(packageIds)
        return toAjax(packageIds.size)
    }

    /**
     * 状态修改
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:edit")
    @Log(title = "租户套餐状态修改", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/changeStatus")
    fun changeStatus(@RequestBody bo: SysTenantPackageBo): R<Void> {
        return toAjax(tenantPackageService.changeStatus(bo))
    }
}
