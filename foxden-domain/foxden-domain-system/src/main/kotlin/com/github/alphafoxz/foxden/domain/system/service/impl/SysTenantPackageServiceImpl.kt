package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysTenantPackageService
import com.github.alphafoxz.foxden.domain.tenant.entity.SysTenantPackage
import com.github.alphafoxz.foxden.domain.system.bo.SysTenantPackageBo
import com.github.alphafoxz.foxden.domain.system.vo.SysTenantPackageVo
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Service

/**
 * TenantPackage 业务层处理
 */
@Service
class SysTenantPackageServiceImpl(
    private val sqlClient: KSqlClient
) : SysTenantPackageService {

    override fun selectTenantPackageList(bo: SysTenantPackageBo): List<SysTenantPackageVo> {
        // TODO: Implement full query when Jimmer DSL is available for tenant module
        // For now, only query by ID
        val packageId = bo.packageId
        if (packageId != null) {
            val pkg = sqlClient.findById(SysTenantPackage::class, packageId)
            return if (pkg != null) listOf(entityToVo(pkg)) else emptyList()
        }

        // Return empty list for now
        return emptyList()
    }

    override fun selectTenantPackageById(packageId: Long): SysTenantPackageVo? {
        val tenantPackage = sqlClient.findById(SysTenantPackage::class, packageId) ?: return null
        return entityToVo(tenantPackage)
    }

    override fun checkPackageNameUnique(bo: SysTenantPackageBo): Boolean {
        // TODO: Implement uniqueness check when Jimmer DSL is available
        return true
    }

    override fun insertTenantPackage(bo: SysTenantPackageBo): Int {
        // TODO: Implement insert using raw SQL or other method when Draft API is not available
        return 0
    }

    override fun updateTenantPackage(bo: SysTenantPackageBo): Int {
        // TODO: Implement update using raw SQL or other method when Draft API is not available
        return 0
    }

    override fun deleteTenantPackageById(packageId: Long) {
        // TODO: Implement delete using raw SQL or other method when Draft API is not available
    }

    override fun deleteTenantPackageByIds(packageIds: Array<Long>) {
        // TODO: Implement delete using raw SQL or other method when Draft API is not available
    }

    override fun changeStatus(bo: SysTenantPackageBo): Int {
        // TODO: Implement status change using raw SQL or other method when Draft API is not available
        return 0
    }

    /**
     * 实体转 VO
     */
    private fun entityToVo(pkg: SysTenantPackage): SysTenantPackageVo {
        return SysTenantPackageVo(
            packageId = pkg.id,
            packageName = pkg.packageName,
            menuIds = pkg.menuIds,
            menuCheckStrictly = pkg.menuCheckStrictly,
            remark = pkg.remark,
            status = pkg.status,
            createTime = pkg.createTime
        )
    }
}
