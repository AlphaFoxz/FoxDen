package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysRoleService
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.bo.SysRoleBo
import com.github.alphafoxz.foxden.domain.system.vo.SysRoleVo
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.*
import org.springframework.stereotype.Service

/**
 * Role 业务层处理
 */
@Service
class SysRoleServiceImpl(
    private val sqlClient: KSqlClient
) : SysRoleService {

    override fun selectPageRoleList(role: SysRoleBo, pageQuery: PageQuery): TableDataInfo<SysRoleVo> {
        // TODO: 使用 fetchPage 实现分页查询
        return TableDataInfo.build()
    }

    override fun selectRoleList(role: SysRoleBo): List<SysRoleVo> {
        val roles = sqlClient.createQuery(SysRole::class) {
            where(table.delFlag eq "0")
            role.roleId?.let { where(table.id eq it) }
            role.roleName?.takeIf { it.isNotBlank() }?.let { where(table.roleName like "%${it}%") }
            role.roleKey?.takeIf { it.isNotBlank() }?.let { where(table.roleKey like "%${it}%") }
            role.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }
            orderBy(table.roleSort.asc())
            select(table)
        }.execute()

        return roles.map { entityToVo(it) }
    }

    override fun selectRolesByUserId(userId: Long): List<SysRoleVo> {
        val user = sqlClient.findById(SysUser::class, userId) ?: return emptyList()
        return user.roles.map { entityToVo(it) }
    }

    override fun selectRolesAuthByUserId(userId: Long): List<SysRoleVo> {
        val user = sqlClient.findById(SysUser::class, userId) ?: return emptyList()
        return user.roles
            .filter { it.status == SystemConstants.NORMAL }
            .map { entityToVo(it) }
    }

    override fun selectRolePermissionByUserId(userId: Long): Set<String> {
        val user = sqlClient.findById(SysUser::class, userId) ?: return emptySet()
        return user.roles
            .filter { it.status == SystemConstants.NORMAL }
            .flatMap { role ->
                role.menus.mapNotNull { menu -> menu.perms }
            }
            .toSet()
    }

    override fun selectRoleAll(): List<SysRoleVo> {
        val roles = sqlClient.createQuery(SysRole::class) {
            where(table.delFlag eq "0")
            orderBy(table.roleSort.asc())
            select(table)
        }.execute()

        return roles.map { entityToVo(it) }
    }

    override fun selectRoleListByUserId(userId: Long): List<Long> {
        val user = sqlClient.findById(SysUser::class, userId) ?: return emptyList()
        return user.roles.map { it.id }
    }

    override fun selectRoleById(roleId: Long): SysRoleVo? {
        val role = sqlClient.findById(SysRole::class, roleId) ?: return null
        return entityToVo(role, withMenus = true, withDepts = true)
    }

    override fun selectRoleByIds(roleIds: List<Long>): List<SysRoleVo> {
        // Workaround: Use findById for each ID
        val roles = roleIds.mapNotNull { sqlClient.findById(SysRole::class, it) }
        return roles.filter { it.delFlag == "0" }.map { entityToVo(it) }
    }

    override fun checkRoleNameUnique(role: SysRoleBo): Boolean {
        val existing = sqlClient.createQuery(SysRole::class) {
            where(table.roleName eq role.roleName)
            role.roleId?.let { where(table.id ne it) }
            select(table)
        }.fetchOneOrNull()

        return existing == null || existing.id == role.roleId
    }

    override fun checkRoleKeyUnique(role: SysRoleBo): Boolean {
        val existing = sqlClient.createQuery(SysRole::class) {
            where(table.roleKey eq role.roleKey)
            role.roleId?.let { where(table.id ne it) }
            select(table)
        }.fetchOneOrNull()

        return existing == null || existing.id == role.roleId
    }

    override fun checkRoleAllowed(role: SysRoleBo) {
        // TODO: 实现 super admin 检查
    }

    override fun checkRoleDataScope(roleId: Long) {
        // TODO: 实现数据权限检查
    }

    override fun checkRoleDataScope(roleIds: List<Long>) {
        // TODO: 实现数据权限检查
    }

    override fun countUserRoleByRoleId(roleId: Long): Long {
        // Simple approach: query all users and count those with the role
        // This is not efficient but works for now
        // TODO: Find a more efficient way using Jimmer DSL for many-to-many relationships
        var count = 0L
        val users = sqlClient.createQuery(SysUser::class) {
            where(table.delFlag eq "0")
            select(table)
        }.execute()

        for (user in users) {
            val userWithRoles = sqlClient.findById(SysUser::class, user.id)
            if (userWithRoles != null && userWithRoles.roles.any { it.id == roleId }) {
                count++
            }
        }

        return count
    }

    override fun insertRole(bo: SysRoleBo): Int {
        val newRole = com.github.alphafoxz.foxden.domain.system.entity.SysRoleDraft.`$`.produce {
            roleName = bo.roleName ?: throw ServiceException("角色名称不能为空")
            roleKey = bo.roleKey ?: throw ServiceException("角色权限字符不能为空")
            roleSort = bo.roleSort ?: 0
            dataScope = bo.dataScope
            menuCheckStrictly = bo.menuCheckStrictly
            deptCheckStrictly = bo.deptCheckStrictly
            status = bo.status ?: SystemConstants.NORMAL
            remark = bo.remark
            delFlag = "0"
            createTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(newRole)
        return if (result.isModified) 1 else 0
    }

    override fun updateRole(bo: SysRoleBo): Int {
        val roleIdVal = bo.roleId ?: return 0
        val existing = sqlClient.findById(SysRole::class, roleIdVal)
            ?: throw ServiceException("角色不存在")

        val updated = com.github.alphafoxz.foxden.domain.system.entity.SysRoleDraft.`$`.produce(existing) {
            bo.roleName?.let { roleName = it }
            bo.roleKey?.let { roleKey = it }
            bo.roleSort?.let { roleSort = it }
            bo.dataScope?.let { dataScope = it }
            bo.menuCheckStrictly?.let { menuCheckStrictly = it }
            bo.deptCheckStrictly?.let { deptCheckStrictly = it }
            bo.status?.let { status = it }
            bo.remark?.let { remark = it }
            updateTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(updated)
        return if (result.isModified) 1 else 0
    }

    override fun updateRoleStatus(roleId: Long, status: String): Int {
        val existing = sqlClient.findById(SysRole::class, roleId)
            ?: throw ServiceException("角色不存在")

        val updated = com.github.alphafoxz.foxden.domain.system.entity.SysRoleDraft.`$`.produce(existing) {
            this.status = status
        }

        val result = sqlClient.save(updated)
        return if (result.isModified) 1 else 0
    }

    override fun deleteRoleById(roleId: Long): Int {
        val result = sqlClient.deleteById(SysRole::class, roleId)
        return result.totalAffectedRowCount.toInt()
    }

    override fun deleteRoleByIds(roleIds: List<Long>): Int {
        val result = sqlClient.deleteByIds(SysRole::class, roleIds)
        return result.totalAffectedRowCount.toInt()
    }

    override fun authDataScope(bo: SysRoleBo): Int {
        // TODO: 实现数据权限授权
        throw ServiceException("功能待实现")
    }

    override fun deleteAuthUser(userId: Long, roleId: Long): Int {
        // TODO: 实现 deleteAuthUser
        throw ServiceException("功能待实现")
    }

    override fun deleteAuthUsers(roleId: Long, userIds: Array<Long>): Int {
        // TODO: 实现 deleteAuthUsers
        throw ServiceException("功能待实现")
    }

    override fun insertAuthUsers(roleId: Long, userIds: Array<Long>): Int {
        // TODO: 实现 insertAuthUsers - 需要研究 Jimmer 多对多关联
        throw ServiceException("功能待实现 - 需要 Jimmer 多对多关联的正确用法")
    }

    override fun cleanOnlineUserByRole(roleId: Long) {
        // TODO: 实现 cleanOnlineUserByRole
    }

    override fun cleanOnlineUser(userIds: List<Long>) {
        // TODO: 实现 cleanOnlineUser
    }

    /**
     * 实体转 VO
     */
    private fun entityToVo(role: SysRole, withMenus: Boolean = false, withDepts: Boolean = false): SysRoleVo {
        return SysRoleVo().apply {
            roleId = role.id
            roleName = role.roleName
            roleKey = role.roleKey
            roleSort = role.roleSort
            dataScope = role.dataScope
            menuCheckStrictly = role.menuCheckStrictly
            deptCheckStrictly = role.deptCheckStrictly
            status = role.status
            remark = role.remark
            createTime = role.createTime
            // Note: createBy/updateBy in entity are Long (user ID), but VO expects String (username)
            // This needs to be resolved by looking up username from user ID
            // For now, skip setting these fields
            updateTime = role.updateTime
            delFlag = role.delFlag.toString()

            if (withMenus) {
                menuIds = role.menus.map { it.id }.toTypedArray()
            }

            if (withDepts) {
                deptIds = role.depts.map { it.id }.toTypedArray()
            }
        }
    }
}
