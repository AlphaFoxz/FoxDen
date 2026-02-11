package com.github.alphafoxz.foxden.domain.system.service.impl

import cn.dev33.satoken.stp.StpUtil
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.domain.system.bo.SysRoleBo
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.service.SysRoleService
import com.github.alphafoxz.foxden.domain.system.vo.SysRoleVo
import org.babyfish.jimmer.sql.kt.*
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Role 业务层处理
 */
@Service
class SysRoleServiceImpl(
    private val sqlClient: KSqlClient,
    private val jdbcTemplate: JdbcTemplate
) : SysRoleService {

    override fun selectPageRoleList(role: SysRoleBo, pageQuery: PageQuery): TableDataInfo<SysRoleVo> {
        val pager = sqlClient.createQuery(SysRole::class) {
            where(table.delFlag eq "0")
            role.roleId?.let { where(table.id eq it) }
            role.roleName?.takeIf { it.isNotBlank() }?.let { where(table.roleName like "%${it}%") }
            role.roleKey?.takeIf { it.isNotBlank() }?.let { where(table.roleKey like "%${it}%") }
            role.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }
            orderBy(table.roleSort.asc())
            select(table)
        }.fetchPage(
            pageQuery.getPageNumOrDefault(),
            pageQuery.getPageSizeOrDefault()
        )

        val roleVos = pager.rows.map { entityToVo(it) }
        return TableDataInfo(roleVos, pager.totalRowCount)
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
        // Query roles through the sys_user_role join table using raw SQL
        val roleIds = jdbcTemplate.queryForList(
            "SELECT role_id FROM sys_user_role WHERE user_id = ?",
            Long::class.java,
            userId
        )

        if (roleIds.isEmpty()) {
            return emptyList()
        }

        // Use findById for each role since IN clause is not available
        val roles = roleIds.mapNotNull { roleId -> sqlClient.findById(SysRole::class, roleId) }
        return roles.map { entityToVo(it) }
    }

    override fun selectRolesAuthByUserId(userId: Long): List<SysRoleVo> {
        // Query roles through the sys_user_role join table using raw SQL
        val roleIds = jdbcTemplate.queryForList(
            "SELECT role_id FROM sys_user_role WHERE user_id = ?",
            Long::class.java,
            userId
        )

        if (roleIds.isEmpty()) {
            return emptyList()
        }

        // Use findById for each role and filter by status
        val roles = roleIds.mapNotNull { roleId -> sqlClient.findById(SysRole::class, roleId) }
            .filter { it.status == SystemConstants.NORMAL }

        return roles.map { entityToVo(it) }
    }

    override fun selectRolePermissionByUserId(userId: Long): Set<String> {
        // Query role IDs through the sys_user_role join table
        val roleIds = jdbcTemplate.queryForList(
            "SELECT role_id FROM sys_user_role WHERE user_id = ?",
            Long::class.java,
            userId
        )

        if (roleIds.isEmpty()) {
            return emptySet()
        }

        // Query menu permissions through the sys_role_menu join table
        val menuPerms = jdbcTemplate.queryForList(
            """
            SELECT DISTINCT m.perms
            FROM sys_menu m
            INNER JOIN sys_role_menu rm ON m.menu_id = rm.menu_id
            WHERE rm.role_id IN (${roleIds.joinToString(",") { "?" }})
            AND m.perms IS NOT NULL
            AND m.perms != ''
            """,
            String::class.java,
            *roleIds.toTypedArray()
        )

        return menuPerms.toSet()
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
        // Query role IDs through the sys_user_role join table using raw SQL
        return jdbcTemplate.queryForList(
            "SELECT role_id FROM sys_user_role WHERE user_id = ?",
            Long::class.java,
            userId
        )
    }

    override fun selectRoleById(roleId: Long): SysRoleVo? {
        val role = sqlClient.findById(SysRole::class, roleId) ?: return null

        // Query menu IDs through the sys_role_menu join table
        val menuIds = jdbcTemplate.queryForList(
            "SELECT menu_id FROM sys_role_menu WHERE role_id = ?",
            Long::class.java,
            roleId
        )

        // Query dept IDs through the sys_role_dept join table
        val deptIds = jdbcTemplate.queryForList(
            "SELECT dept_id FROM sys_role_dept WHERE role_id = ?",
            Long::class.java,
            roleId
        )

        val vo = entityToVo(role, withMenus = false, withDepts = false)
        vo.menuIds = menuIds.toTypedArray()
        vo.deptIds = deptIds.toTypedArray()
        return vo
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
        // Count users with this role through the sys_user_role join table
        return jdbcTemplate.queryForObject(
            "SELECT COUNT(DISTINCT user_id) FROM sys_user_role WHERE role_id = ?",
            Long::class.java,
            roleId
        ) ?: 0L
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
        return result.totalAffectedRowCount
    }

    override fun deleteRoleByIds(roleIds: List<Long>): Int {
        val result = sqlClient.deleteByIds(SysRole::class, roleIds)
        return result.totalAffectedRowCount
    }

    override fun authDataScope(bo: SysRoleBo): Int {
        val roleId = bo.roleId ?: return 0

        // 1. 先删除角色原有的部门关联
        jdbcTemplate.update("DELETE FROM sys_role_dept WHERE role_id = ?", roleId)

        // 2. 如果有部门权限配置，插入新的关联
        val deptIds = bo.deptIds
        if (!deptIds.isNullOrEmpty() && bo.dataScope != null) {
            val deptCheckStrictly = bo.deptCheckStrictly ?: false

            deptIds.forEach { deptId ->
                jdbcTemplate.update(
                    "INSERT INTO sys_role_dept (role_id, dept_id) VALUES (?, ?)",
                    roleId,
                    deptId
                )
            }
        }

        // 3. 更新角色的数据权限配置
        val rows = sqlClient.createUpdate(SysRole::class) {
            where(table.id eq roleId)
            set(table.dataScope, bo.dataScope)
            set(table.deptCheckStrictly, bo.deptCheckStrictly)
            set(table.updateTime, java.time.LocalDateTime.now())
        }.execute()

        // 4. 清理该角色的在线用户
        if (rows > 0) {
            cleanOnlineUserByRole(roleId)
        }

        return rows
    }

    @Transactional
    override fun deleteAuthUser(userId: Long, roleId: Long): Int {
        // 不允许修改当前登录用户的角色
        val currentUserId = LoginHelper.getUserId()
        if (currentUserId != null && currentUserId == userId) {
            throw ServiceException("不允许修改当前用户角色!")
        }

        // 删除用户角色关联
        val rows = jdbcTemplate.update(
            "DELETE FROM sys_user_role WHERE user_id = ? AND role_id = ?",
            userId, roleId
        )

        // 如果删除成功，清理该用户的在线会话
        if (rows > 0) {
            cleanOnlineUser(listOf(userId))
        }

        return rows
    }

    @Transactional
    override fun deleteAuthUsers(roleId: Long, userIds: Array<Long>): Int {
        val ids = userIds.toList()

        // 不允许修改当前登录用户的角色
        val currentUserId = LoginHelper.getUserId()
        if (currentUserId != null && ids.contains(currentUserId)) {
            throw ServiceException("不允许修改当前用户角色!")
        }

        // 批量删除用户角色关联
        val inClause = userIds.joinToString(",") { "?" }
        val params = mutableListOf(roleId)
        params.addAll(userIds.toList())
        val rows = jdbcTemplate.update(
            "DELETE FROM sys_user_role WHERE role_id = ? AND user_id IN ($inClause)",
            *params.toTypedArray()
        )

        // 如果删除成功，清理这些用户的在线会话
        if (rows > 0) {
            cleanOnlineUser(ids)
        }

        return rows
    }

    @Transactional
    override fun insertAuthUsers(roleId: Long, userIds: Array<Long>): Int {
        val ids = userIds.toList()

        // 不允许修改当前登录用户的角色
        val currentUserId = LoginHelper.getUserId()
        if (currentUserId != null && ids.contains(currentUserId)) {
            throw ServiceException("不允许修改当前用户角色!")
        }

        // 批量插入用户角色关联
        var rows = 0
        ids.forEach { userId ->
            try {
                // 先检查是否已存在关联
                val count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM sys_user_role WHERE user_id = ? AND role_id = ?",
                    Int::class.java,
                    userId, roleId
                )

                if (count == 0) {
                    // 插入新关联
                    val result = jdbcTemplate.update(
                        "INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)",
                        userId, roleId
                    )
                    rows += result
                }
            } catch (e: Exception) {
                // 忽略重复键错误
            }
        }

        // 如果插入成功，清理这些用户的在线会话（需要重新登录获取新角色）
        if (rows > 0) {
            cleanOnlineUser(ids)
        }

        return rows
    }

    override fun cleanOnlineUserByRole(roleId: Long) {
        // 查询该角色绑定的用户数量
        val userCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM sys_user_role WHERE role_id = ?",
            Int::class.java,
            roleId
        ) ?: 0

        if (userCount == 0) {
            return
        }

        // 获取所有在线token
        val keys = StpUtil.searchTokenValue("", 0, -1, false)
        if (keys.isEmpty()) {
            return
        }

        // 角色关联的在线用户量过大时会导致redis阻塞，需要谨慎操作
        keys.parallelStream().forEach { key ->
            val token = key.substringAfterLast(":")
            // 如果已经过期则跳过
            if (StpUtil.stpLogic.getTokenActiveTimeoutByToken(token) < -1) {
                return@forEach
            }

            try {
                val loginId = StpUtil.getLoginIdByToken(token)
                // 检查该用户是否拥有当前角色
                val hasRole = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM sys_user_role WHERE user_id = ? AND role_id = ?",
                    Int::class.java,
                    loginId.toString().toLong(), roleId
                ) ?: 0

                if (hasRole > 0) {
                    // 踢出该用户
                    StpUtil.logoutByTokenValue(token)
                }
            } catch (e: Exception) {
                // token可能已失效，忽略错误
            }
        }
    }

    override fun cleanOnlineUser(userIds: List<Long>) {
        if (userIds.isEmpty()) {
            return
        }

        // 获取所有在线token
        val keys = StpUtil.searchTokenValue("", 0, -1, false)
        if (keys.isEmpty()) {
            return
        }

        keys.parallelStream().forEach { key ->
            val token = key.substringAfterLast(":")
            // 如果已经过期则跳过
            if (StpUtil.stpLogic.getTokenActiveTimeoutByToken(token) < -1) {
                return@forEach
            }

            try {
                val loginId = StpUtil.getLoginIdByToken(token)

                // 检查该用户是否在需要清理的列表中
                if (userIds.contains(loginId.toString().toLong())) {
                    // 踢出该用户
                    StpUtil.logoutByTokenValue(token)
                }
            } catch (e: Exception) {
                // token可能已失效，忽略错误
            }
        }
    }

    /**
     * 实体转 VO
     *
     * Note: withMenus and withDepts parameters are kept for compatibility,
     * but the actual loading is handled by the calling methods using raw SQL queries
     * to avoid Jimmer lazy loading UnloadedException
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

            // menuIds and deptIds should be set by calling methods using raw SQL queries
            // to avoid lazy loading issues
        }
    }
}
