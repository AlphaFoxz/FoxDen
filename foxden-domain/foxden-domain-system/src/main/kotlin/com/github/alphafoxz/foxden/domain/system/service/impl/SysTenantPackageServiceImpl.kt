package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysTenantPackageService
import org.springframework.stereotype.Service
import com.github.alphafoxz.foxden.domain.system.bo.SysTenantPackageBo
import com.github.alphafoxz.foxden.domain.system.vo.SysTenantPackageVo

/**
 * TenantPackage 业务层处理
 */
@Service
class SysTenantPackageServiceImpl(): SysTenantPackageService {

    override fun selectTenantPackageList(bo: SysTenantPackageBo): List<SysTenantPackageVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectTenantPackageById(packageId: Long): SysTenantPackageVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun checkPackageNameUnique(bo: SysTenantPackageBo): Boolean {
        // TODO: 实现业务逻辑
        return true
    }

    override fun insertTenantPackage(bo: SysTenantPackageBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun updateTenantPackage(bo: SysTenantPackageBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun deleteTenantPackageById(packageId: Long) {
        // TODO: 实现业务逻辑
    }

    override fun deleteTenantPackageByIds(packageIds: Array<Long>) {
        // TODO: 实现业务逻辑
    }

    override fun changeStatus(bo: SysTenantPackageBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }
}
