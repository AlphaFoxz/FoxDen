package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysRoleService
import org.springframework.stereotype.Service
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysRoleBo
import com.github.alphafoxz.foxden.domain.system.vo.SysRoleVo

/**
 * Role 业务层处理
 */
@Service
class SysRoleServiceImpl(): SysRoleService {

    override fun selectPageRoleList(role: SysRoleBo, pageQuery: PageQuery): TableDataInfo<SysRoleVo> {
        // TODO: 实现业务逻辑
        return com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo.build(emptyList())
    }

    override fun selectRoleList(role: SysRoleBo): List<SysRoleVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectRolesByUserId(userId: Long): List<SysRoleVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectRolesAuthByUserId(userId: Long): List<SysRoleVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectRolePermissionByUserId(userId: Long): Set<String> {
    return emptySet()
        // TODO: 实现业务逻辑
    }

    override fun selectRoleAll(): List<SysRoleVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectRoleListByUserId(userId: Long): List<Long> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectRoleById(roleId: Long): SysRoleVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun selectRoleByIds(roleIds: List<Long>): List<SysRoleVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun checkRoleNameUnique(role: SysRoleBo): Boolean {
        // TODO: 实现业务逻辑
        return true
    }

    override fun checkRoleKeyUnique(role: SysRoleBo): Boolean {
        // TODO: 实现业务逻辑
        return true
    }

    override fun checkRoleAllowed(role: SysRoleBo) {
        // TODO: 实现业务逻辑
    }

    override fun checkRoleDataScope(roleId: Long) {
        // TODO: 实现业务逻辑
    }

    override fun checkRoleDataScope(roleIds: List<Long>) {
        // TODO: 实现业务逻辑
    }

    override fun countUserRoleByRoleId(roleId: Long): Long {
        // TODO: 实现业务逻辑
        return 0L
    }

    override fun insertRole(bo: SysRoleBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun updateRole(bo: SysRoleBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun updateRoleStatus(roleId: Long, status: String): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun authDataScope(bo: SysRoleBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun deleteRoleById(roleId: Long): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun deleteRoleByIds(roleIds: List<Long>): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun deleteAuthUser(userId: Long, roleId: Long): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun deleteAuthUsers(roleId: Long, userIds: Array<Long>): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun insertAuthUsers(roleId: Long, userIds: Array<Long>): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun cleanOnlineUserByRole(roleId: Long) {
        // TODO: 实现业务逻辑
    }

    override fun cleanOnlineUser(userIds: List<Long>) {
        // TODO: 实现业务逻辑
    }
}
