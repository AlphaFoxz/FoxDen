package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysPermissionService
import org.springframework.stereotype.Service
import com.github.alphafoxz.foxden.domain.system.vo.RouterVo
import com.github.alphafoxz.foxden.domain.system.vo.SysMenuVo

/**
 * Permission 业务层处理
 */
@Service
class SysPermissionServiceImpl(): SysPermissionService {

    override fun getRoleCustom(userId: Long, roleIds: Array<Long>): String {
    return ""
        // TODO: 实现业务逻辑
    }

    override fun getRoleCustom(roleIds: Array<Long>): String {
    return ""
        // TODO: 实现业务逻辑
    }

    override fun getRolePermission(userId: Long, roleIds: Array<Long>): Set<String> {
    return emptySet()
        // TODO: 实现业务逻辑
    }

    override fun getRolePermission(roleIds: Array<Long>): Set<String> {
    return emptySet()
        // TODO: 实现业务逻辑
    }

    override fun getMenuPermission(userId: Long, roleIds: Array<Long>): Set<String> {
    return emptySet()
        // TODO: 实现业务逻辑
    }

    override fun getMenuPermission(roleIds: Array<Long>): Set<String> {
    return emptySet()
        // TODO: 实现业务逻辑
    }

    override fun buildMenusByUserId(userId: Long): List<RouterVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun buildMenuTreeByUserId(userId: Long): List<SysMenuVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }
}
