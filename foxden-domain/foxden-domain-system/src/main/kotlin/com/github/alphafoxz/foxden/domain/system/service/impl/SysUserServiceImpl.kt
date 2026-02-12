package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.domain.dto.UserDTO
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.service.UserService
import com.github.alphafoxz.foxden.common.core.utils.MapstructUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.core.enums.UserType
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
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
import java.util.Date

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
) : SysUserService, UserService {

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
            pageQuery.getPageNumOrDefault() - 1,  // Jimmer fetchPage expects 0-based index
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
        if (LoginHelper.isSuperAdmin(userId)) {
            throw ServiceException("不允许操作超级管理员用户")
        }
    }

    override fun checkUserDataScope(userId: Long) {
        // 超级管理员不检查数据权限
        if (LoginHelper.isSuperAdmin()) {
            return
        }
        // 检查用户是否存在
        val user = sqlClient.findById(SysUser::class, userId)
        if (user == null) {
            throw ServiceException("没有权限访问用户数据！")
        }
    }

    override fun insertUserAuth(userId: Long, roleIds: Array<Long>) {
        if (roleIds.isEmpty()) {
            return
        }

        val roleList = roleIds.toMutableList()

        // 非超级管理员，禁止包含超级管理员角色
        if (!LoginHelper.isSuperAdmin(userId)) {
            roleList.remove(SystemConstants.SUPER_ADMIN_ID)
        }

        // 先删除该用户的所有角色关联
        jdbcTemplate.update("DELETE FROM sys_user_role WHERE user_id = ?", userId)

        // 插入新的角色关联
        if (roleList.isNotEmpty()) {
            val insertValues = roleList.joinToString(",") { "(?,?)" }
            val insertArgs = mutableListOf<Any?>()

            for (roleId in roleList) {
                insertArgs.add(userId)
                insertArgs.add(roleId)
            }

            jdbcTemplate.update(
                "INSERT INTO sys_user_role (user_id, role_id) VALUES $insertValues",
                *insertArgs.toTypedArray()
            )
        }
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
        if (!result.isModified) {
            return 0
        }

        // 设置返回的 userId
        user.userId = result.modifiedEntity.id

        // 新增用户岗位关联
        insertUserPost(user, false)

        return 1
    }

    /**
     * 新增用户岗位信息
     *
     * @param user  用户对象
     * @param clear 清除已存在的关联数据
     */
    private fun insertUserPost(user: SysUserBo, clear: Boolean) {
        val postIdList = user.postIds ?: return

        if (postIdList.isEmpty()) {
            return
        }

        val userId = user.userId ?: return

        // 是否清除旧的用户岗位绑定
        if (clear) {
            jdbcTemplate.update(
                "DELETE FROM sys_user_post WHERE user_id = ?",
                userId
            )
        }

        // 批量插入用户-岗位关联
        val insertValues = postIdList.joinToString(",") { "(?,?)" }
        val insertArgs = mutableListOf<Any?>()

        for (postId in postIdList) {
            insertArgs.add(userId)
            insertArgs.add(postId)
        }

        jdbcTemplate.update(
            "INSERT INTO sys_user_post (user_id, post_id) VALUES $insertValues",
            *insertArgs.toTypedArray()
        )
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

        // 新增用户与角色管理
        insertUserRole(userIdVal, user.roleIds?.toTypedArray(), true)

        // 新增用户与岗位管理
        insertUserPost(user, true)

        val result = sqlClient.createUpdate(SysUser::class) {
            where(table.id eq userIdVal)
            user.deptId?.let { set(table.deptId, it) }
            user.nickName?.let { set(table.nickName, it) }
            user.email?.let { set(table.email, it) }
            user.phonenumber?.let { set(table.phonenumber, it) }
            user.sex?.let { set(table.sex, it) }
            user.status?.let { set(table.status, it) }
            user.remark?.let { set(table.remark, it) }
            user.userType?.let { set(table.userType, it) }
            set(table.updateTime, LocalDateTime.now())
        }.execute()

        if (result < 1) {
            throw ServiceException("修改用户${user.userName}信息失败")
        }

        return result
    }

    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     * @param clear   清除已存在的关联数据
     */
    private fun insertUserRole(userId: Long, roleIds: Array<Long>?, clear: Boolean) {
        if (roleIds == null || roleIds.isEmpty()) {
            return
        }

        val roleList = roleIds.toMutableList()

        // 非超级管理员，禁止包含超级管理员角色
        if (!LoginHelper.isSuperAdmin(userId)) {
            roleList.remove(SystemConstants.SUPER_ADMIN_ID)
        }

        // 是否清除原有绑定
        if (clear) {
            jdbcTemplate.update(
                "DELETE FROM sys_user_role WHERE user_id = ?",
                userId
            )
        }

        // 批量插入用户-角色关联
        if (roleList.isNotEmpty()) {
            val insertValues = roleList.joinToString(",") { "(?,?)" }
            val insertArgs = mutableListOf<Any?>()

            for (roleId in roleList) {
                insertArgs.add(userId)
                insertArgs.add(roleId)
            }

            jdbcTemplate.update(
                "INSERT INTO sys_user_role (user_id, role_id) VALUES $insertValues",
                *insertArgs.toTypedArray()
            )
        }
    }

    override fun updateUserStatus(userId: Long, status: String): Int {
        val result = sqlClient.createUpdate(SysUser::class) {
            where(table.id eq userId)
            set(table.status, status)
        }.execute()
        return result
    }

    override fun updateUserProfile(user: SysUserBo): Int {
        val userIdVal = user.userId ?: return 0

        val result = sqlClient.createUpdate(SysUser::class) {
            where(table.id eq userIdVal)
            user.nickName?.let { set(table.nickName, it) }
            user.email?.let { set(table.email, it) }
            user.phonenumber?.let { set(table.phonenumber, it) }
            user.sex?.let { set(table.sex, it) }
            user.remark?.let { set(table.remark, it) }
            set(table.updateTime, LocalDateTime.now())
        }.execute()
        return result
    }

    override fun updateUserAvatar(userId: Long, avatar: Long): Boolean {
        val result = sqlClient.createUpdate(SysUser::class) {
            where(table.id eq userId)
            set(table.avatar, avatar)
        }.execute()
        return result > 0
    }

    override fun resetUserPwd(userId: Long, password: String): Int {
        val result = sqlClient.createUpdate(SysUser::class) {
            where(table.id eq userId)
            set(table.password, password)
        }.execute()
        return result
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

    // ==================== UserService 接口新增方法 ====================

    /**
     * 通过用户ID查询用户账户
     *
     * @param userId 用户ID
     * @return 用户账户
     */
    override fun selectUserNameById(userId: Long): String? {
        val user = sqlClient.createQuery(SysUser::class) {
            where(table.id eq userId)
            select(table.userName)
        }.fetchOneOrNull()
        return user
    }

    /**
     * 通过用户ID查询用户昵称
     *
     * @param userId 用户ID
     * @return 用户昵称
     */
    override fun selectNicknameById(userId: Long): String? {
        val user = sqlClient.createQuery(SysUser::class) {
            where(table.id eq userId)
            select(table.nickName)
        }.fetchOneOrNull()
        return user
    }

    /**
     * 通过用户ID查询用户手机号
     *
     * @param userId 用户id
     * @return 用户手机号
     */
    override fun selectPhonenumberById(userId: Long): String? {
        val user = sqlClient.createQuery(SysUser::class) {
            where(table.id eq userId)
            select(table.phonenumber)
        }.fetchOneOrNull()
        return user
    }

    /**
     * 通过用户ID查询用户邮箱
     *
     * @param userId 用户id
     * @return 用户邮箱
     */
    override fun selectEmailById(userId: Long): String? {
        val user = sqlClient.createQuery(SysUser::class) {
            where(table.id eq userId)
            select(table.email)
        }.fetchOneOrNull()
        return user
    }

    /**
     * 通过用户ID查询用户列表
     *
     * @param userIds 用户ids
     * @return 用户列表
     */
    override fun selectListByIds(userIds: List<Long>): List<UserDTO> {
        if (userIds.isEmpty()) {
            return emptyList()
        }

        // 使用 findByIds 避免类型推断问题
        val users = sqlClient.findByIds(SysUser::class, userIds)
            .filter { it.status == SystemConstants.NORMAL }

        return users.map { user ->
            UserDTO(
                userId = user.id,
                deptId = user.deptId,
                userName = user.userName,
                nickName = user.nickName,
                userType = user.userType,
                email = user.email,
                phonenumber = user.phonenumber,
                sex = user.sex,
                status = user.status,
                createTime = user.createTime?.let { localDateTimeToDate(it) }
            )
        }
    }

    /**
     * LocalDateTime 转换为 Date
     */
    private fun localDateTimeToDate(localDateTime: LocalDateTime): Date {
        return java.sql.Timestamp.valueOf(localDateTime)
    }

    /**
     * 通过角色ID查询用户ID
     *
     * @param roleIds 角色ids
     * @return 用户ids
     */
    override fun selectUserIdsByRoleIds(roleIds: List<Long>): List<Long> {
        if (roleIds.isEmpty()) {
            return emptyList()
        }

        // 查询 sys_user_role 表获取用户ID列表
        val userIds = mutableSetOf<Long>()
        roleIds.forEach { roleId ->
            val ids = jdbcTemplate.queryForList(
                "SELECT user_id FROM sys_user_role WHERE role_id = ?",
                Long::class.java,
                roleId
            )
            userIds.addAll(ids)
        }
        return userIds.toList()
    }

    /**
     * 通过角色ID查询用户
     *
     * @param roleIds 角色ids
     * @return 用户
     */
    override fun selectUsersByRoleIds(roleIds: List<Long>): List<UserDTO> {
        if (roleIds.isEmpty()) {
            return emptyList()
        }

        val userIds = selectUserIdsByRoleIds(roleIds)
        if (userIds.isEmpty()) {
            return emptyList()
        }

        return selectListByIds(userIds)
    }

    /**
     * 通过部门ID查询用户
     *
     * @param deptIds 部门ids
     * @return 用户
     */
    override fun selectUsersByDeptIds(deptIds: List<Long>): List<UserDTO> {
        if (deptIds.isEmpty()) {
            return emptyList()
        }

        // 使用 JDBC 查询，避免 `in` 关键字冲突
        val sql = StringBuilder(
            """
            SELECT user_id, user_name, nick_name, email, phonenumber
            FROM sys_user
            WHERE del_flag = '0' AND status = '0' AND dept_id IN (
            """.trimIndent()
        )

        val params = mutableListOf<Any?>()
        deptIds.forEachIndexed { index, deptId ->
            sql.append(if (index == 0) "?" else ",?")
            params.add(deptId)
        }
        sql.append(")")

        val users = jdbcTemplate.query(
            sql.toString(),
            params.toTypedArray()
        ) { rs, _ ->
            UserDTO(
                userId = rs.getLong("user_id"),
                userName = rs.getString("user_name"),
                nickName = rs.getString("nick_name"),
                email = rs.getString("email"),
                phonenumber = rs.getString("phonenumber")
            )
        }

        return users
    }

    /**
     * 通过岗位ID查询用户
     *
     * @param postIds 岗位ids
     * @return 用户
     */
    override fun selectUsersByPostIds(postIds: List<Long>): List<UserDTO> {
        if (postIds.isEmpty()) {
            return emptyList()
        }

        // 查询 sys_user_post 表获取用户ID列表
        val userIds = mutableSetOf<Long>()
        postIds.forEach { postId ->
            val ids = jdbcTemplate.queryForList(
                "SELECT user_id FROM sys_user_post WHERE post_id = ?",
                Long::class.java,
                postId
            )
            userIds.addAll(ids)
        }

        if (userIds.isEmpty()) {
            return emptyList()
        }

        return selectListByIds(userIds.toList())
    }

    /**
     * 根据用户 ID 列表查询用户名称映射关系
     *
     * @param userIds 用户 ID 列表
     * @return Map，其中 key 为用户 ID，value 为对应的用户名称
     */
    override fun selectUserNamesByIds(userIds: List<Long>): Map<Long, String> {
        if (userIds.isEmpty()) {
            return emptyMap()
        }

        val result = mutableMapOf<Long, String>()

        // 使用 JDBC 查询，避免 Jimmer DSL 的类型推断问题
        if (userIds.size == 1) {
            val nickName = sqlClient.findById(SysUser::class, userIds[0])?.nickName
            if (nickName != null) {
                result[userIds[0]] = nickName
            }
        } else {
            val users = sqlClient.findByIds(SysUser::class, userIds)
            for (user in users) {
                result[user.id] = user.nickName ?: ""
            }
        }

        return result
    }
}
