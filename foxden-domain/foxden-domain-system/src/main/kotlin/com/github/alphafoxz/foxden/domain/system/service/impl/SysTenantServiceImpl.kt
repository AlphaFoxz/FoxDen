package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.bo.SysTenantBo
import com.github.alphafoxz.foxden.domain.system.service.SysTenantService
import com.github.alphafoxz.foxden.domain.system.vo.SysTenantVo
import org.springframework.stereotype.Service

/**
 * 租户 业务层处理
 */
@Service
class SysTenantServiceImpl(): SysTenantService {

    override fun selectTenantList(bo: SysTenantBo): List<SysTenantVo> {
        return emptyList()
    }

    override fun queryByTenantId(tenantId: String): SysTenantVo? {
        return null
    }

    override fun checkTenantAllowed(tenantId: String) {
    }

    override fun checkTenantNameUnique(bo: SysTenantBo): Boolean {
        return true
    }

    override fun checkTenantUserNameUnique(bo: SysTenantBo): Boolean {
        return true
    }

    override fun insertTenant(bo: SysTenantBo): Int {
        return 0
    }

    override fun updateTenant(bo: SysTenantBo): Int {
        return 0
    }

    override fun updateTenantStatus(tenantId: String, status: String) {
    }

    override fun deleteTenantById(tenantId: String) {
    }

    override fun syncTenantPackage(tenantIds: Array<String>) {
    }
}
