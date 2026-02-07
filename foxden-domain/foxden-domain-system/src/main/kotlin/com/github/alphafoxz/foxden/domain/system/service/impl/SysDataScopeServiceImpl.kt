package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysDataScopeService
import org.springframework.stereotype.Service

/**
 * DataScope 业务层处理
 */
@Service
class SysDataScopeServiceImpl(): SysDataScopeService {

    override fun selectDeptIdsByRoleId(roleId: Long): List<Long> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectDeptIdsByRoleIds(roleIds: Array<Long>): List<Long> {
        // TODO: 实现业务逻辑
        return emptyList()
    }
}
