package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.MapstructUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.core.enums.UserType
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysUserBo
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.service.SysUserService
import com.github.alphafoxz.foxden.domain.system.service.SysRoleService
import com.github.alphafoxz.foxden.domain.system.vo.SysUserExportVo
import com.github.alphafoxz.foxden.domain.system.vo.SysUserVo
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * User 业务层处理
 *
 * @description 使用 Jimmer DSL 实现查询方法（简化版）
 * @see com.github.alphafoxz.foxden.domain.system.service.SysUserService
 */
@Service
class SysUserServiceImpl(
    private val sqlClient: KSqlClient,
    private val roleService: SysRoleService,
    private val jdbcTemplate: JdbcTemplate
) : SysUserService {

    override fun selectPageUserList(user: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
        val pager = sqlClient.createQuery(SysUser::class) {
            where(table.delFlag eq "0")
            user.userId?.let { where(table.id eq it) }
            user.userName?.takeIf { it.isNotBlank() }?.let { where(table.userName like "%${it}%") }
            user.nickName?.takeIf { it.isNotBlank() }?.let { where(table.nickName like "%${it}%") }
            user.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }
            user.phonenumber?.takeIf { it.isNotBlank() }?.let { where(table.phonenumber eq it) }
            orderBy(table.id.asc())
            select(table)
        }.fetchPage(
            pageQuery.getPageNumOrDefault(),
            pageQuery.getPageSizeOrDefault()
        )

        val userVos = pager.rows.map { entityToVo(it) }
        return TableDataInfo(userVos, pager.totalRowCount)
    }

    override fun selectUserExportList(user: SysUserBo): List<SysUserExportVo> {
        val users = sqlClient.createQuery(SysUser::class) {
            where(table.delFlag eq "0")
            user.userId?.let { where(table.id eq it) }
            user.userName?.takeIf { it.isNotBlank() }?.let { where(table.userName like "%${it}%") }
            user.nickName?.takeIf { it.isNotBlank() }?.let { where(table.nickName like "%${it}%") }
            user.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }
            user.phonenumber?.takeIf { it.isNotBlank() }?.let { where(table.phonenumber eq it) }
            orderBy(table.id.asc())
            select(table)
        }.execute()

        return users.mapNotNull { MapstructUtils.convert(it, SysUserExportVo::class.java) }
    }

    override fun selectAllocatedList(user: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
        // 查询已分配到指定角色的用户列表
        val roleId = user.roleId ?: return TableDataInfo.build()

        // 构建SQL查询
        val sql = StringBuilder(
            """
            SELECT DISTINCT u.user_id FROM sys_user u
            INNER JOIN sys_user_role ur ON u.user_id = ur.user_id
            WHERE u.del_flag = '0' AND ur.role_id = ?
            """.trimIndent()
        )

        val params = mutableListOf<Any?>()

        // 添加动态条件
        user.userName?.takeIf { it.isNotBlank() }?.let {
            sql.append(" AND u.user_name LIKE CONCAT('%', ?, '%')")
            params.add(it)
        }
        user.status?.takeIf { it.isNotBlank() }?.let {
            sql.append(" AND u.status = ?")
            params.add(it)
        }
        user.phonenumber?.takeIf { it.isNotBlank() }?.let {
            sql.append(" AND u.phonenumber LIKE CONCAT('%', ?, '%')")
            params.add(it)
        }

        sql.append(" ORDER BY u.user_id ASC")

        // 查询用户ID列表
        val allParams = mutableListOf<Any?>(roleId)
        allParams.addAll(params)
        val userIds = jdbcTemplate.queryForList(sql.toString(), Long::class.java, *allParams.toTypedArray())

        // 查询用户详细信息
        val users = userIds.mapNotNull { userId ->
            sqlClient.findById(SysUser::class, userId)
        }

        // 手动分页
        val total = users.size.toLong()
        val pageSize = pageQuery.pageSize ?: 10
        val pageNum = pageQuery.pageNum ?: 1
        val start = ((pageNum - 1) * pageSize).toInt()
        val end = minOf(start + pageSize, total.toInt())
        val pagedUsers = if (start < total) users.subList(start, end) else emptyList()

        return TableDataInfo(pagedUsers.map { entityToVo(it) }, total)
    }

    override fun selectUnallocatedList(user: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
        // 查询未分配到指定角色的用户列表
        val roleId = user.roleId ?: return TableDataInfo.build()

        // 先查询已分配的用户ID
        val allocatedUserIds = jdbcTemplate.queryForList(
            "SELECT ur.user_id FROM sys_user_role ur WHERE ur.role_id = ?",
            Long::class.java,
            roleId
        ).toSet()

        // 构建SQL查询
        val sql = StringBuilder(
            """
            SELECT u.user_id FROM sys_user u
            LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id AND ur.role_id = ?
            WHERE u.del_flag = '0' AND ur.role_id IS NULL
            """.trimIndent()
        )

        val params = mutableListOf<Any?>(roleId)

        // 排除已分配的用户
        if (allocatedUserIds.isNotEmpty()) {
            sql.append(" AND u.user_id NOT IN (")
            sql.append(allocatedUserIds.joinToString(",") { "?" })
            sql.append(")")
            params.addAll(allocatedUserIds)
        }

        // 添加动态条件
        user.userName?.takeIf { it.isNotBlank() }?.let {
            sql.append(" AND u.user_name LIKE CONCAT('%', ?, '%')")
            params.add(it)
        }
        user.phonenumber?.takeIf { it.isNotBlank() }?.let {
            sql.append(" AND u.phonenumber LIKE CONCAT('%', ?, '%')")
            params.add(it)
        }

        sql.append(" ORDER BY u.user_id ASC")

        // 查询用户ID列表
        val userIds = jdbcTemplate.queryForList(sql.toString(), Long::class.java, *params.toTypedArray())

        // 查询用户详细信息
        val users = userIds.mapNotNull { userId ->
            sqlClient.findById(SysUser::class, userId)
        }

        // 手动分页
        val total = users.size.toLong()
        val pageSize = pageQuery.pageSize ?: 10
        val pageNum = pageQuery.pageNum ?: 1
        val start = ((pageNum - 1) * pageSize).toInt()
        val end = minOf(start + pageSize, total.toInt())
        val pagedUsers = if (start < total) users.subList(start, end) else emptyList()

        return TableDataInfo(pagedUsers.map { entityToVo(it) }, total)
    }

    override fun selectUserByUserName(userName: String): SysUserVo? {
        val user = sqlClient.createQuery(SysUser::class) {
            where(table.userName eq userName)
            select(table)
        }.fetchOneOrNull() ?: return null

        return entityToVo(user)
    }

    override fun selectUserByPhonenumber(phonenumber: String): SysUserVo? {
        val user = sqlClient.createQuery(SysUser::class) {
            where(table.phonenumber eq phonenumber)
            select(table)
        }.fetchOneOrNull() ?: return null

        return entityToVo(user)
    }

    override fun selectUserById(userId: Long): SysUserVo? {
        val user = sqlClient.findById(SysUser::class, userId) ?: return null
        val vo = entityToVo(user, withRoles = false)

        // 手动查询角色数据（类似 ruoyi 的方式）
        if (vo.userId != null) {
            vo.roles = roleService.selectRolesByUserId(vo.userId!!)
        }

        return vo
    }

    override fun selectUserByEmail(email: String): SysUserVo? {
        val user = sqlClient.createQuery(SysUser::class) {
            where(table.email eq email)
            select(table)
        }.fetchOneOrNull() ?: return null

        return entityToVo(user)
    }

    override fun selectUserByIds(userIds: List<Long>, deptId: Long?): List<SysUserVo> {
        // Workaround: Use findByIds for each ID
        // TODO: Find the correct Jimmer DSL syntax for IN queries
        val users = userIds.mapNotNull { sqlClient.findById(SysUser::class, it) }
        return if (deptId != null) {
            users.filter { it.deptId == deptId && it.status == "0" }
        } else {
            users.filter { it.status == "0" }
        }.map { entityToVo(it) }
    }

    override fun selectUserRoleGroup(userId: Long): String {
        val user = sqlClient.findById(SysUser::class, userId) ?: return ""
        return user.roles.mapNotNull { it.roleName }.joinToString(",")
    }

    override fun selectUserPostGroup(userId: Long): String {
        val user = sqlClient.findById(SysUser::class, userId) ?: return ""
        return user.posts.mapNotNull { it.postName }.joinToString(",")
    }

    override fun checkUserNameUnique(user: SysUserBo): Boolean {
        val existing = sqlClient.createQuery(SysUser::class) {
            where(table.userName eq user.userName)
            user.userId?.let { where(table.id ne it) }
            select(table)
        }.fetchOneOrNull()

        return existing == null || existing.id == user.userId
    }

    override fun checkPhoneUnique(user: SysUserBo): Boolean {
        val existing = sqlClient.createQuery(SysUser::class) {
            where(table.phonenumber eq user.phonenumber)
            user.userId?.let { where(table.id ne it) }
            select(table)
        }.fetchOneOrNull()

        return existing == null || existing.id == user.userId
    }

    override fun checkEmailUnique(user: SysUserBo): Boolean {
        val existing = sqlClient.createQuery(SysUser::class) {
            where(table.email eq user.email)
            user.userId?.let { where(table.id ne it) }
            select(table)
        }.fetchOneOrNull()

        return existing == null || existing.id == user.userId
    }

    override fun checkUserAllowed(userId: Long) {
        // TODO: 实现 super admin 检查
    }

    override fun checkUserDataScope(userId: Long) {
        // TODO: 实现数据权限检查
    }

    override fun insertUser(user: SysUserBo): Int {
        val newUser = com.github.alphafoxz.foxden.domain.system.entity.SysUserDraft.`$`.produce {
            userName = user.userName ?: throw ServiceException("用户名不能为空")
            nickName = user.nickName
            email = user.email
            phonenumber = user.phonenumber
            password = user.password
            sex = user.sex
            status = user.status ?: SystemConstants.NORMAL
            deptId = user.deptId
            remark = user.remark
            userType = user.userType
            avatar = null
            delFlag = "0"
            createTime = LocalDateTime.now()
        }

        val result = sqlClient.save(newUser)
        return if (result.isModified) 1 else 0
    }

    override fun registerUser(user: SysUserBo, tenantId: String?): Boolean {
        val newUser = com.github.alphafoxz.foxden.domain.system.entity.SysUserDraft.`$`.produce {
            userName = user.userName ?: throw ServiceException("用户名不能为空")
            nickName = user.nickName
            email = user.email
            phonenumber = user.phonenumber
            password = user.password
            sex = user.sex
            status = SystemConstants.NORMAL
            deptId = user.deptId
            remark = user.remark
            userType = UserType.SYS_USER.userType
            avatar = null
            if (tenantId != null) {
                this.tenantId = tenantId
            }
            delFlag = "0"
            createTime = LocalDateTime.now()
        }

        val result = sqlClient.save(newUser)
        return result.isModified
    }

    override fun updateUser(user: SysUserBo): Int {
        val userIdVal = user.userId ?: return 0
        val existing = sqlClient.findById(SysUser::class, userIdVal)
            ?: throw ServiceException("用户不存在")

        val updated = com.github.alphafoxz.foxden.domain.system.entity.SysUserDraft.`$`.produce(existing) {
            user.deptId?.let { deptId = it }
            user.nickName?.let { nickName = it }
            user.email?.let { email = it }
            user.phonenumber?.let { phonenumber = it }
            user.sex?.let { sex = it }
            user.status?.let { status = it }
            user.remark?.let { remark = it }
            user.userType?.let { userType = it }
            // Note: updateBy expects Long (user ID), but we only have String (username)
            // This needs to be resolved by looking up the user ID from the username
            // For now, skip setting updateBy
            updateTime = LocalDateTime.now()
        }

        val result = sqlClient.save(updated)
        return if (result.isModified) 1 else 0
    }

    override fun insertUserAuth(userId: Long, roleIds: Array<Long>) {
        val existing = sqlClient.findById(SysUser::class, userId)
            ?: throw ServiceException("用户不存在")

        // TODO: Need to research proper Jimmer API for many-to-many association updates
        // The Draft API's roles() list expects SysRoleDraft objects, but produce() returns immutable SysRole
        // This requires further investigation into Jimmer's association handling
        throw ServiceException("角色分配功能待实现 - 需要 Jimmer 多对多关联的正确用法")
    }

    override fun updateUserStatus(userId: Long, status: String): Int {
        val existing = sqlClient.findById(SysUser::class, userId)
            ?: throw ServiceException("用户不存在")

        val updated = com.github.alphafoxz.foxden.domain.system.entity.SysUserDraft.`$`.produce(existing) {
            this.status = status
        }

        val result = sqlClient.save(updated)
        return if (result.isModified) 1 else 0
    }

    override fun updateUserProfile(user: SysUserBo): Int {
        val userIdVal = user.userId ?: return 0
        val existing = sqlClient.findById(SysUser::class, userIdVal)
            ?: throw ServiceException("用户不存在")

        val updated = com.github.alphafoxz.foxden.domain.system.entity.SysUserDraft.`$`.produce(existing) {
            user.nickName?.let { nickName = it }
            user.email?.let { email = it }
            user.phonenumber?.let { phonenumber = it }
            user.sex?.let { sex = it }
            user.remark?.let { remark = it }
            // Note: updateBy expects Long (user ID), but we only have String (username)
            // This needs to be resolved by looking up the user ID from the username
            updateTime = LocalDateTime.now()
        }

        val result = sqlClient.save(updated)
        return if (result.isModified) 1 else 0
    }

    override fun updateUserAvatar(userId: Long, avatar: Long): Boolean {
        val existing = sqlClient.findById(SysUser::class, userId)
            ?: throw ServiceException("用户不存在")

        val updated = com.github.alphafoxz.foxden.domain.system.entity.SysUserDraft.`$`.produce(existing) {
            this.avatar = avatar
        }

        val result = sqlClient.save(updated)
        return result.isModified
    }

    override fun resetUserPwd(userId: Long, password: String): Int {
        val existing = sqlClient.findById(SysUser::class, userId)
            ?: throw ServiceException("用户不存在")

        val updated = com.github.alphafoxz.foxden.domain.system.entity.SysUserDraft.`$`.produce(existing) {
            this.password = password
        }

        val result = sqlClient.save(updated)
        return if (result.isModified) 1 else 0
    }

    override fun validatePassword(userName: String, password: String): Boolean {
        val user = sqlClient.createQuery(SysUser::class) {
            where(table.userName eq userName)
            select(table)
        }.fetchOneOrNull() ?: return false

        val hashedPassword = user.password ?: return false
        return cn.hutool.crypto.digest.BCrypt.checkpw(password, hashedPassword)
    }

    override fun deleteUserById(userId: Long): Int {
        val result = sqlClient.deleteById(SysUser::class, userId)
        return result.totalAffectedRowCount.toInt()
    }

    override fun deleteUserByIds(userIds: Array<Long>): Int {
        val result = sqlClient.deleteByIds(SysUser::class, userIds.toList())
        return result.totalAffectedRowCount.toInt()
    }

    override fun selectUserListByDept(deptId: Long): List<SysUserVo> {
        return sqlClient.createQuery(SysUser::class) {
            where(table.deptId eq deptId)
            orderBy(table.id.asc())
            select(table)
        }.execute().map { entityToVo(it) }
    }

    /**
     * 实体转 VO
     */
    private fun entityToVo(user: SysUser, withRoles: Boolean = false): SysUserVo {
        return SysUserVo().apply {
            userId = user.id
            deptId = user.deptId
            userName = user.userName
            nickName = user.nickName
            userType = user.userType
            email = user.email
            phonenumber = user.phonenumber
            sex = user.sex
            status = user.status
            remark = user.remark
            loginDate = user.loginDate?.toInstant()?.atZone(java.time.ZoneId.systemDefault())?.toLocalDateTime()
            createTime = user.createTime

            if (withRoles) {
                roles = user.roles.map { role ->
                    com.github.alphafoxz.foxden.domain.system.vo.SysRoleVo().apply {
                        roleId = role.id
                        roleName = role.roleName
                        roleKey = role.roleKey
                        roleSort = role.roleSort
                        dataScope = role.dataScope
                        menuCheckStrictly = role.menuCheckStrictly
                        deptCheckStrictly = role.deptCheckStrictly
                        status = role.status
                        createTime = role.createTime
                    }
                }
            }
        }
    }

    /**
     * 根据用户ID字符串获取对应的用户账号列表（逗号分隔）
     *
     * @param userIds 以逗号分隔的用户ID字符串
     * @return 用户账号字符串（逗号分隔）
     */
    override fun selectUserNameByIds(userIds: String): String? {
        if (userIds.isBlank()) {
            return null
        }

        val ids = userIds.split(",").mapNotNull { it.toLongOrNull() }
        val names = mutableListOf<String>()
        for (id in ids) {
            val vo = selectUserById(id)
            if (vo != null) {
                names.add(vo.userName ?: "")
            }
        }

        return names.joinToString(",")
    }

    /**
     * 根据用户ID字符串获取对应的用户昵称列表（逗号分隔）
     *
     * @param userIds 以逗号分隔的用户ID字符串
     * @return 用户昵称字符串（逗号分隔）
     */
    override fun selectNicknameByIds(userIds: String): String? {
        if (userIds.isBlank()) {
            return null
        }

        val ids = userIds.split(",").mapNotNull { it.toLongOrNull() }
        val names = mutableListOf<String>()
        for (id in ids) {
            val vo = selectUserById(id)
            if (vo != null) {
                names.add(vo.nickName ?: "")
            }
        }

        return names.joinToString(",")
    }
}
