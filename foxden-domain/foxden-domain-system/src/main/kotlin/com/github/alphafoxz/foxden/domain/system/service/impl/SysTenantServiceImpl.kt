package com.github.alphafoxz.foxden.domain.system.service.impl

import cn.hutool.core.util.RandomUtil
import cn.hutool.crypto.digest.BCrypt
import com.github.alphafoxz.foxden.common.core.constant.CacheNames
import com.github.alphafoxz.foxden.common.core.constant.Constants
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.constant.TenantConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.jimmer.helper.TenantHelper
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.domain.system.bo.SysTenantBo
import com.github.alphafoxz.foxden.domain.system.service.SysTenantPackageService
import com.github.alphafoxz.foxden.domain.system.service.SysTenantService
import com.github.alphafoxz.foxden.domain.system.service.extensions.saveWithAutoId
import com.github.alphafoxz.foxden.domain.system.vo.SysTenantVo
import com.github.alphafoxz.foxden.domain.tenant.entity.SysTenantPackage
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.cache.annotation.Cacheable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 租户 业务层处理
 */
@Service
class SysTenantServiceImpl(
    private val sqlClient: KSqlClient,
    private val tenantPackageService: SysTenantPackageService,
    private val jdbcTemplate: JdbcTemplate
) : SysTenantService {

    override fun selectTenantList(bo: SysTenantBo): List<SysTenantVo> {
        val conditions = buildConditions(bo)
        val sql = """
            SELECT t.* FROM sys_tenant t
            WHERE t.del_flag = '0'
            $conditions
            ORDER BY t.id ASC
        """.trimIndent()

        val args = buildArgs(bo)
        return jdbcTemplate.query(sql, args.toTypedArray()) { rs, _ ->
            entityToVo(rs)
        }
    }

    private fun buildConditions(bo: SysTenantBo): String {
        val conditions = mutableListOf<String>()
        bo.tenantId?.takeIf { it.isNotBlank() }?.let { conditions.add("AND t.tenant_id = ?") }
        bo.contactUserName?.takeIf { it.isNotBlank() }?.let { conditions.add("AND t.contact_user_name LIKE ?") }
        bo.contactPhone?.takeIf { it.isNotBlank() }?.let { conditions.add("AND t.contact_phone = ?") }
        bo.companyName?.takeIf { it.isNotBlank() }?.let { conditions.add("AND t.company_name LIKE ?") }
        bo.domain?.takeIf { it.isNotBlank() }?.let { conditions.add("AND t.domain LIKE ?") }
        bo.packageId?.let { conditions.add("AND t.package_id = ?") }
        bo.status?.takeIf { it.isNotBlank() }?.let { conditions.add("AND t.status = ?") }
        return conditions.joinToString(" \n")
    }

    private fun buildArgs(bo: SysTenantBo): MutableList<Any?> {
        val args = mutableListOf<Any?>()
        bo.tenantId?.takeIf { it.isNotBlank() }?.let { args.add(it) }
        bo.contactUserName?.takeIf { it.isNotBlank() }?.let { args.add("%${bo.contactUserName}%") }
        bo.contactPhone?.takeIf { it.isNotBlank() }?.let { args.add(bo.contactPhone) }
        bo.companyName?.takeIf { it.isNotBlank() }?.let { args.add("%${bo.companyName}%") }
        bo.domain?.takeIf { it.isNotBlank() }?.let { args.add("%${bo.domain}%") }
        bo.packageId?.let { args.add(it) }
        bo.status?.takeIf { it.isNotBlank() }?.let { args.add(bo.status) }
        return args
    }

    override fun queryById(id: Long): SysTenantVo? {
        val sql = """
            SELECT t.* FROM sys_tenant t
            WHERE t.id = ? AND t.del_flag = '0'
            ORDER BY t.id ASC
        """.trimIndent()

        return jdbcTemplate.query(sql, arrayOf(id)) { rs, _ ->
            entityToVo(rs)
        }.firstOrNull()
    }

    @Cacheable(cacheNames = [CacheNames.SYS_TENANT], key = "#tenantId")
    override fun queryByTenantId(tenantId: String): SysTenantVo? {
        val sql = """
            SELECT t.* FROM sys_tenant t
            WHERE t.tenant_id = ? AND t.del_flag = '0'
            ORDER BY t.id ASC
        """.trimIndent()

        return jdbcTemplate.query(sql, arrayOf(tenantId)) { rs, _ ->
            entityToVo(rs)
        }.firstOrNull()
    }

    override fun checkTenantAllowed(tenantId: String) {
        if (tenantId.isNotBlank() && TenantConstants.DEFAULT_TENANT_ID == tenantId) {
            throw ServiceException("不允许操作管理租户")
        }
    }

    override fun checkTenantNameUnique(bo: SysTenantBo): Boolean {
        val sql = """
            SELECT COUNT(*) FROM sys_tenant
            WHERE company_name = ? AND del_flag = '0'
            ${bo.tenantId?.takeIf { it.isNotBlank() }?.let { "AND tenant_id != '$it'" } ?: ""}
        """.trimIndent()

        val count = jdbcTemplate.queryForObject(sql, Int::class.java, bo.companyName ?: "") ?: 0
        return count == 0
    }

    override fun checkTenantUserNameUnique(bo: SysTenantBo): Boolean {
        // 用户名校验在用户服务中处理
        return true
    }

    @Transactional
    override fun insertTenant(bo: SysTenantBo): Int {
        // 1. 生成唯一租户ID
        val existingTenantIds = jdbcTemplate.query(
            "SELECT tenant_id FROM sys_tenant WHERE del_flag = '0'",
            emptyArray()
        ) { rs, _ -> rs.getString("tenant_id") }

        val tenantId = generateTenantId(existingTenantIds)

        // 2. 使用JdbcTemplate直接插入租户
        jdbcTemplate.update(
            """INSERT INTO sys_tenant (
                tenant_id, contact_user_name, contact_phone, company_name,
                license_number, address, domain, intro, package_id,
                account_count, status, del_flag, create_by, create_time
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '0', ?, ?)""",
            tenantId,
            bo.contactUserName ?: throw ServiceException("联系人不能为空"),
            bo.contactPhone ?: throw ServiceException("联系电话不能为空"),
            bo.companyName ?: throw ServiceException("企业名称不能为空"),
            bo.licenseNumber,
            bo.address,
            bo.domain,
            bo.intro,
            bo.packageId,
            bo.accountCount?.toLong(),
            bo.status ?: "0",
            bo.createBy?.toLong(),
            java.time.LocalDateTime.now()
        )

        // 3. 根据套餐创建租户角色
        val roleId = createTenantRole(
            tenantId, bo.packageId
                ?: throw ServiceException("套餐ID不能为空")
        )

        // 4. 创建部门（企业名称作为部门名）
        val dept = com.github.alphafoxz.foxden.domain.system.entity.SysDeptDraft.`$`.produce {
            this.tenantId = tenantId
            deptName = bo.companyName ?: "默认部门"
            parentId = Constants.TOP_PARENT_ID
            ancestors = Constants.TOP_PARENT_ID.toString()
            status = "0"
            delFlag = "0"
            createBy = bo.createBy?.toLong()
            createTime = java.time.LocalDateTime.now()
        }
        val deptResult = sqlClient.save(dept)
        if (!deptResult.isModified) {
            throw ServiceException("创建部门失败")
        }
        val deptId = deptResult.modifiedEntity?.id

        // 5. 创建角色-部门关联
        jdbcTemplate.update(
            "INSERT INTO sys_role_dept (role_id, dept_id) VALUES (?, ?)",
            roleId, deptId
        )

        // 6. 创建管理员用户
        val user = com.github.alphafoxz.foxden.domain.system.entity.SysUserDraft.`$`.produce {
            this.tenantId = tenantId
            userName = bo.username ?: throw ServiceException("用户名不能为空")
            nickName = bo.nickName ?: bo.username
            password = BCrypt.hashpw(bo.password ?: throw ServiceException("密码不能为空"))
            this.deptId = deptId
            status = "0"
            delFlag = "0"
            createBy = bo.createBy?.toLong()
            createTime = java.time.LocalDateTime.now()
        }
        val userResult = sqlClient.saveWithAutoId(user)
        if (!userResult.isModified) {
            throw ServiceException("创建用户失败")
        }
        val userId = userResult.modifiedEntity?.id

        // 7. 更新部门负责人
        jdbcTemplate.update(
            "UPDATE sys_dept SET leader = ? WHERE dept_id = ?",
            userId, deptId
        )

        // 8. 创建用户-角色关联
        jdbcTemplate.update(
            "INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)",
            userId, roleId
        )

        // 9. 同步字典数据
        syncTenantDictData(tenantId)

        // 10. 同步配置数据
        syncTenantConfigData(tenantId)

        return 1
    }

    /**
     * 根据租户菜单创建租户角色
     *
     * @param tenantId  租户编号
     * @param packageId 租户套餐id
     * @return 角色id
     */
    private fun createTenantRole(tenantId: String, packageId: Long): Long {
        // 获取租户套餐
        val tenantPackage = sqlClient.findById(SysTenantPackage::class, packageId)
            ?: throw ServiceException("套餐不存在")

        // 获取套餐菜单id
        val menuIdsStr = tenantPackage.menuIds
        val menuIds = if (menuIdsStr.isNullOrBlank()) {
            emptyList()
        } else {
            menuIdsStr.split(",").mapNotNull { it.toLongOrNull() }
        }

        // 创建角色
        val role = com.github.alphafoxz.foxden.domain.system.entity.SysRoleDraft.`$`.produce {
            this.tenantId = tenantId
            roleName = TenantConstants.TENANT_ADMIN_ROLE_NAME
            roleKey = TenantConstants.TENANT_ADMIN_ROLE_KEY
            roleSort = 1
            status = SystemConstants.NORMAL
            delFlag = "0"
            createBy = LoginHelper.getUserId()?.toLong()
            createTime = java.time.LocalDateTime.now()
        }
        val roleResult = sqlClient.save(role)
        if (!roleResult.isModified) {
            throw ServiceException("创建角色失败")
        }
        val roleId = roleResult.modifiedEntity?.id ?: throw ServiceException("角色ID获取失败")

        // 创建角色菜单关联
        if (menuIds.isNotEmpty()) {
            val placeholders = menuIds.joinToString(",") { "(?, ?)" }
            val args = mutableListOf<Any>(roleId)
            menuIds.forEach { args.add(it) }
            jdbcTemplate.update(
                "INSERT INTO sys_role_menu (role_id, menu_id) VALUES $placeholders",
                *args.toTypedArray()
            )
        }

        return roleId
    }

    /**
     * 同步租户字典数据
     *
     * @param tenantId 租户ID
     */
    private fun syncTenantDictData(tenantId: String) {
        // 使用JdbcTemplate从默认租户获取字典类型
        val dictTypes = jdbcTemplate.query(
            "SELECT * FROM sys_dict_type WHERE tenant_id = ? AND del_flag = '0'",
            arrayOf(TenantConstants.DEFAULT_TENANT_ID)
        ) { rs, _ ->
            com.github.alphafoxz.foxden.domain.system.entity.SysDictTypeDraft.`$`.produce {
                dictName = rs.getString("dict_name")
                this.dictType = rs.getString("dict_type")
                remark = rs.getString("remark")
                this.tenantId = tenantId
                createBy = rs.getLong("create_by").takeIf { !rs.wasNull() }
                createTime = java.time.LocalDateTime.now()
            }
        }

        // 使用JdbcTemplate从默认租户获取字典数据
        val dictDataList = jdbcTemplate.query(
            "SELECT * FROM sys_dict_data WHERE tenant_id = ? AND del_flag = '0'",
            arrayOf(TenantConstants.DEFAULT_TENANT_ID)
        ) { rs, _ ->
            com.github.alphafoxz.foxden.domain.system.entity.SysDictDataDraft.`$`.produce {
                dictSort = rs.getInt("dict_sort")
                dictLabel = rs.getString("dict_label")
                dictValue = rs.getString("dict_value")
                this.dictType = rs.getString("dict_type")
                cssClass = rs.getString("css_class")
                listClass = rs.getString("list_class")
                defaultFlag = rs.getString("default_flag")
                remark = rs.getString("remark")
                this.tenantId = tenantId
                createBy = rs.getLong("create_by").takeIf { !rs.wasNull() }
                createTime = java.time.LocalDateTime.now()
            }
        }

        // 保存字典类型
        for (dictType in dictTypes) {
            sqlClient.save(dictType)
        }

        // 保存字典数据
        for (dictData in dictDataList) {
            sqlClient.save(dictData)
        }
    }

    /**
     * 同步租户配置数据
     *
     * @param tenantId 租户ID
     */
    private fun syncTenantConfigData(tenantId: String) {
        // 使用JdbcTemplate从默认租户获取配置
        val configs = jdbcTemplate.query(
            "SELECT * FROM sys_config WHERE tenant_id = ? AND del_flag = '0'",
            arrayOf(TenantConstants.DEFAULT_TENANT_ID)
        ) { rs, _ ->
            com.github.alphafoxz.foxden.domain.system.entity.SysConfigDraft.`$`.produce {
                configName = rs.getString("config_name")
                configKey = rs.getString("config_key")
                configValue = rs.getString("config_value")
                configType = rs.getString("config_type")
                remark = rs.getString("remark")
                this.tenantId = tenantId
                createBy = rs.getLong("create_by").takeIf { !rs.wasNull() }
                createTime = java.time.LocalDateTime.now()
            }
        }

        // 保存配置
        for (config in configs) {
            sqlClient.save(config)
        }
    }

    override fun updateTenant(bo: SysTenantBo): Int {
        val tenantId = bo.tenantId ?: return 0

        // 检查租户是否存在
        val existingCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM sys_tenant WHERE tenant_id = ? AND del_flag = '0'",
            Int::class.java,
            tenantId
        ) ?: 0

        if (existingCount == 0) {
            throw ServiceException("租户不存在")
        }

        // 校验企业名称唯一性
        if (!checkTenantNameUnique(bo)) {
            throw ServiceException("企业名称已存在")
        }

        // 使用JdbcTemplate更新
        val updates = mutableListOf<String>()
        val values = mutableListOf<Any?>()

        bo.contactUserName?.let { updates.add("contact_user_name = ?"); values.add(it) }
        bo.contactPhone?.let { updates.add("contact_phone = ?"); values.add(it) }
        bo.companyName?.let { updates.add("company_name = ?"); values.add(it) }
        bo.licenseNumber?.let { updates.add("license_number = ?"); values.add(it) }
        bo.address?.let { updates.add("address = ?"); values.add(it) }
        bo.domain?.let { updates.add("domain = ?"); values.add(it) }
        bo.intro?.let { updates.add("intro = ?"); values.add(it) }
        bo.remark?.let { updates.add("remark = ?"); values.add(it) }
        bo.expireTime?.let { updates.add("expire_time = ?"); values.add(java.sql.Timestamp.valueOf(it)) }
        bo.accountCount?.let { updates.add("account_count = ?"); values.add(it.toLong()) }
        bo.createBy?.let { updates.add("update_by = ?"); values.add(it.toLong()) }
        updates.add("update_time = ?")
        values.add(java.time.LocalDateTime.now())

        if (updates.isEmpty()) return 0

        values.add(tenantId)
        val sql = """
            UPDATE sys_tenant
            SET ${updates.joinToString(", ")}
            WHERE tenant_id = ?
        """.trimIndent()

        return jdbcTemplate.update(sql, *values.toTypedArray())
    }

    override fun updateTenantStatus(tenantId: String, status: String): Int {
        return jdbcTemplate.update(
            "UPDATE sys_tenant SET status = ?, update_time = ? WHERE tenant_id = ?",
            status, java.time.LocalDateTime.now(), tenantId
        )
    }

    override fun deleteTenantById(tenantId: String) {
        // 不允许删除默认租户
        if (TenantConstants.DEFAULT_TENANT_ID == tenantId) {
            throw ServiceException("不能删除系统默认租户")
        }

        // 软删除
        jdbcTemplate.update(
            "UPDATE sys_tenant SET del_flag = '1', update_time = ? WHERE tenant_id = ?",
            java.time.LocalDateTime.now(), tenantId
        )
    }

    override fun deleteWithValidByIds(ids: Collection<Long>, isValid: Boolean): Boolean {
        if (isValid) {
            // 校验是否包含超管租户（id为1）
            if (ids.contains(1L)) {
                throw ServiceException("超管租户不能删除")
            }
        }
        // 批量软删除
        val placeholders = ids.joinToString(",") { "?" }
        jdbcTemplate.update(
            "UPDATE sys_tenant SET del_flag = '1', update_time = ? WHERE id IN ($placeholders)",
            mutableListOf<Any>(java.time.LocalDateTime.now()).apply { addAll(ids) }.toTypedArray()
        )
        return true
    }

    @Transactional
    override fun syncTenantPackage(tenantId: String, packageId: Long): Boolean {
        // 使用 TenantHelper.ignore 忽略租户过滤
        return TenantHelper.ignore {
            val tenant = jdbcTemplate.query(
                "SELECT * FROM sys_tenant WHERE tenant_id = ? AND del_flag = '0'",
                arrayOf(tenantId)
            ) { rs, _ ->
                com.github.alphafoxz.foxden.domain.tenant.entity.SysTenantDraft.`$`.produce {
                    this.tenantId = rs.getString("tenant_id")
                    this.packageId = rs.getLong("package_id").takeIf { !rs.wasNull() }
                }
            }.firstOrNull() ?: throw ServiceException("租户不存在")

            // 获取套餐信息
            val tenantPackage = tenantPackageService.selectTenantPackageById(packageId)
                ?: throw ServiceException("套餐不存在")

            // 获取套餐菜单id
            val menuIdsStr = tenantPackage.menuIds ?: ""
            val menuIds = if (menuIdsStr.isNullOrBlank()) {
                emptyList()
            } else {
                menuIdsStr.split(",").mapNotNull { it.toLongOrNull() }
            }

            // 使用JdbcTemplate获取租户的所有角色
            val roles: List<Pair<Long, String>> = jdbcTemplate.query(
                "SELECT * FROM sys_role WHERE tenant_id = ? AND del_flag = '0'",
                arrayOf(tenantId)
            ) { rs, _ ->
                Pair(rs.getLong("id"), rs.getString("role_key"))
            }

            val otherRoleIds = mutableListOf<Long>()

            for (role in roles) {
                val roleId = role.first
                val roleKey = role.second
                if (roleKey == TenantConstants.TENANT_ADMIN_ROLE_KEY) {
                    // 租户管理员：完全替换菜单权限
                    // 先删除旧的菜单关联
                    jdbcTemplate.update(
                        "DELETE FROM sys_role_menu WHERE role_id = ?",
                        roleId
                    )

                    // 创建新的菜单关联
                    if (menuIds.isNotEmpty()) {
                        val placeholders = menuIds.joinToString(",") { "(?, ?)" }
                        val args = mutableListOf<Any>(roleId)
                        menuIds.forEach { args.add(it) }
                        jdbcTemplate.update(
                            "INSERT INTO sys_role_menu (role_id, menu_id) VALUES $placeholders",
                            *args.toTypedArray()
                        )
                    }
                } else {
                    // 其他角色：移除不在套餐中的菜单
                    otherRoleIds.add(roleId)
                }
            }

            // 对于其他角色，移除不在套餐中的菜单
            if (otherRoleIds.isNotEmpty() && menuIds.isNotEmpty()) {
                val menuPlaceholders = menuIds.joinToString(",") { "?" }
                val rolePlaceholders = otherRoleIds.joinToString(",") { "?" }

                jdbcTemplate.update(
                    "DELETE FROM sys_role_menu WHERE role_id IN ($rolePlaceholders) " +
                            "AND menu_id NOT IN ($menuPlaceholders)",
                    *otherRoleIds.toTypedArray(),
                    *menuIds.toTypedArray()
                )
            }

            true
        } ?: false
    }

    @Transactional
    override fun syncTenantPackage(tenantIds: Array<String>) {
        if (tenantIds.isEmpty()) return

        for (tenantId in tenantIds) {
            // 使用JdbcTemplate获取租户信息
            val tenant = jdbcTemplate.query(
                "SELECT * FROM sys_tenant WHERE tenant_id = ? AND del_flag = '0'",
                arrayOf(tenantId)
            ) { rs, _ ->
                com.github.alphafoxz.foxden.domain.tenant.entity.SysTenantDraft.`$`.produce {
                    this.tenantId = rs.getString("tenant_id")
                    packageId = rs.getLong("package_id").takeIf { !rs.wasNull() }
                }
            }.firstOrNull() ?: continue

            val packageId = tenant.packageId ?: continue

            // 获取套餐信息
            val tenantPackage = tenantPackageService.selectTenantPackageById(packageId)
                ?: continue

            // 获取套餐菜单id
            val menuIdsStr = tenantPackage.menuIds ?: ""
            val menuIds = if (menuIdsStr.isNullOrBlank()) {
                emptyList()
            } else {
                menuIdsStr.split(",").mapNotNull { it.toLongOrNull() }
            }

            // 使用JdbcTemplate获取租户的所有角色
            val roles: List<Pair<Long, String>> = jdbcTemplate.query(
                "SELECT * FROM sys_role WHERE tenant_id = ? AND del_flag = '0'",
                arrayOf(tenantId)
            ) { rs, _ ->
                Pair(rs.getLong("id"), rs.getString("role_key"))
            }

            val otherRoleIds = mutableListOf<Long>()

            for (role in roles) {
                val roleId = role.first
                val roleKey = role.second
                if (roleKey == TenantConstants.TENANT_ADMIN_ROLE_KEY) {
                    // 租户管理员：完全替换菜单权限
                    // 先删除旧的菜单关联
                    jdbcTemplate.update(
                        "DELETE FROM sys_role_menu WHERE role_id = ?",
                        roleId
                    )

                    // 创建新的菜单关联
                    if (menuIds.isNotEmpty()) {
                        val placeholders = menuIds.joinToString(",") { "(?, ?)" }
                        val args = mutableListOf<Any>(roleId)
                        menuIds.forEach { args.add(it) }
                        jdbcTemplate.update(
                            "INSERT INTO sys_role_menu (role_id, menu_id) VALUES $placeholders",
                            *args.toTypedArray()
                        )
                    }
                } else {
                    // 其他角色：移除不在套餐中的菜单
                    otherRoleIds.add(roleId)
                }
            }

            // 对于其他角色，移除不在套餐中的菜单
            if (otherRoleIds.isNotEmpty() && menuIds.isNotEmpty()) {
                val menuPlaceholders = menuIds.joinToString(",") { "?" }
                val rolePlaceholders = otherRoleIds.joinToString(",") { "?" }

                jdbcTemplate.update(
                    "DELETE FROM sys_role_menu WHERE role_id IN ($rolePlaceholders) " +
                            "AND menu_id NOT IN ($menuPlaceholders)",
                    *otherRoleIds.toTypedArray(),
                    *menuIds.toTypedArray()
                )
            }
        }
    }

    @Transactional
    override fun syncTenantDict() {
        // 从默认租户获取所有字典类型
        val defaultDictTypes = jdbcTemplate.query(
            "SELECT * FROM sys_dict_type WHERE tenant_id = ? AND del_flag = '0'",
            arrayOf(TenantConstants.DEFAULT_TENANT_ID)
        ) { rs, _ ->
            com.github.alphafoxz.foxden.domain.system.entity.SysDictTypeDraft.`$`.produce {
                dictName = rs.getString("dict_name")
                this.dictType = rs.getString("dict_type")
                remark = rs.getString("remark")
                this.tenantId = TenantConstants.DEFAULT_TENANT_ID
                createBy = rs.getLong("create_by").takeIf { !rs.wasNull() }
                createTime = java.time.LocalDateTime.now()
            }
        }

        // 从默认租户获取所有字典数据
        val defaultDictDataList = jdbcTemplate.query(
            "SELECT * FROM sys_dict_data WHERE tenant_id = ? AND del_flag = '0'",
            arrayOf(TenantConstants.DEFAULT_TENANT_ID)
        ) { rs, _ ->
            com.github.alphafoxz.foxden.domain.system.entity.SysDictDataDraft.`$`.produce {
                dictSort = rs.getInt("dict_sort")
                dictLabel = rs.getString("dict_label")
                dictValue = rs.getString("dict_value")
                this.dictType = rs.getString("dict_type")
                cssClass = rs.getString("css_class")
                listClass = rs.getString("list_class")
                defaultFlag = rs.getString("default_flag")
                remark = rs.getString("remark")
                this.tenantId = TenantConstants.DEFAULT_TENANT_ID
                createBy = rs.getLong("create_by").takeIf { !rs.wasNull() }
                createTime = java.time.LocalDateTime.now()
            }
        }

        // 获取所有正常状态的租户
        val tenants = jdbcTemplate.query(
            "SELECT * FROM sys_tenant WHERE status = '0' AND del_flag = '0'",
        ) { rs, _ -> rs.getString("tenant_id") }

        // 按租户分组的字典类型和数据
        val tenantDictTypesMap = jdbcTemplate.query(
            "SELECT * FROM sys_dict_type WHERE del_flag = '0'",
            emptyArray()
        ) { rs, _ ->
            Pair(rs.getString("tenant_id"), rs.getString("dict_type"))
        }.groupBy { it.first }.mapValues { entry -> entry.value.map { pair -> pair.second } }

        val tenantDictDataMap = jdbcTemplate.query(
            "SELECT * FROM sys_dict_data WHERE del_flag = '0'",
            emptyArray()
        ) { rs, _ ->
            Triple(rs.getString("tenant_id"), rs.getString("dict_type"), rs.getString("dict_value"))
        }.groupBy { it.first }.mapValues { entry ->
            entry.value.groupBy { it.second }
        }

        for (tenant in tenants) {
            if (tenant == TenantConstants.DEFAULT_TENANT_ID) continue

            val existingDictTypes = tenantDictTypesMap[tenant]?.toSet() ?: emptySet()
            val existingDictData = tenantDictDataMap[tenant] ?: emptyMap()

            for (dictType in defaultDictTypes) {
                // 如果字典类型不存在，则创建
                if (!existingDictTypes.contains(dictType.dictType)) {
                    val newDictType = com.github.alphafoxz.foxden.domain.system.entity.SysDictTypeDraft.`$`.produce {
                        dictName = dictType.dictName
                        this.dictType = dictType.dictType
                        remark = dictType.remark
                        this.tenantId = tenant
                        createBy = dictType.createBy
                        createTime = java.time.LocalDateTime.now()
                    }
                    sqlClient.save(newDictType)
                }
            }

            for (defaultData in defaultDictDataList) {
                // 获取租户已有的该类型的字典值
                val existingValues: Set<String> = existingDictData[defaultData.dictType]
                    ?.map { it.third }?.filterNotNull()?.toSet() ?: emptySet()

                // 如果字典值不存在，则创建
                if (!existingValues.contains(defaultData.dictValue)) {
                    val newDictData = com.github.alphafoxz.foxden.domain.system.entity.SysDictDataDraft.`$`.produce {
                        dictSort = defaultData.dictSort
                        dictLabel = defaultData.dictLabel
                        dictValue = defaultData.dictValue
                        this.dictType = defaultData.dictType
                        cssClass = defaultData.cssClass
                        listClass = defaultData.listClass
                        defaultFlag = defaultData.defaultFlag
                        remark = defaultData.remark
                        this.tenantId = tenant
                        createBy = defaultData.createBy
                        createTime = java.time.LocalDateTime.now()
                    }
                    sqlClient.save(newDictData)
                }
            }
        }
    }

    @Transactional
    override fun syncTenantConfig() {
        // 从默认租户获取所有配置
        val defaultConfigs: List<com.github.alphafoxz.foxden.domain.system.entity.SysConfig> = jdbcTemplate.query(
            "SELECT * FROM sys_config WHERE tenant_id = ? AND del_flag = '0'",
            arrayOf(TenantConstants.DEFAULT_TENANT_ID)
        ) { rs, _ ->
            com.github.alphafoxz.foxden.domain.system.entity.SysConfigDraft.`$`.produce {
                configName = rs.getString("config_name")
                configKey = rs.getString("config_key")
                configValue = rs.getString("config_value")
                configType = rs.getString("config_type")
                remark = rs.getString("remark")
                this.tenantId = TenantConstants.DEFAULT_TENANT_ID
                createBy = rs.getLong("create_by").takeIf { !rs.wasNull() }
                createTime = java.time.LocalDateTime.now()
            }
        }

        // 按租户分组的配置
        val tenantConfigsMap: Map<String, List<String>> = jdbcTemplate.query(
            "SELECT * FROM sys_config WHERE del_flag = '0'",
            emptyArray()
        ) { rs, _ ->
            Pair(rs.getString("tenant_id"), rs.getString("config_key"))
        }.groupBy({ it.first }, { it.second })

        // 获取所有正常状态的租户
        val tenants: List<String> = jdbcTemplate.query(
            "SELECT tenant_id FROM sys_tenant WHERE status = '0' AND del_flag = '0'",
            emptyArray()
        ) { rs, _ -> rs.getString("tenant_id") }

        for (tenant in tenants) {
            if (tenant == TenantConstants.DEFAULT_TENANT_ID) continue

            val existingConfigs: Set<String> = tenantConfigsMap[tenant]?.toSet() ?: emptySet()

            for (config in defaultConfigs) {
                // 如果配置不存在，则创建
                if (!(config.configKey in existingConfigs)) {
                    val newConfig = com.github.alphafoxz.foxden.domain.system.entity.SysConfigDraft.`$`.produce {
                        configName = config.configName
                        configKey = config.configKey
                        configValue = config.configValue
                        configType = config.configType
                        remark = config.remark
                        this.tenantId = tenant
                        createBy = config.createBy
                        createTime = java.time.LocalDateTime.now()
                    }
                    sqlClient.save(newConfig)
                }
            }
        }
    }

    /**
     * 生成租户ID（6位随机数）
     *
     * @param existingTenantIds 已存在的租户ID列表
     * @return 唯一的租户ID
     */
    private fun generateTenantId(existingTenantIds: List<String>): String {
        var tenantId = RandomUtil.randomNumbers(6)
        var retryCount = 0
        val maxRetry = 100

        while (existingTenantIds.contains(tenantId) && retryCount < maxRetry) {
            tenantId = RandomUtil.randomNumbers(6)
            retryCount++
        }

        if (retryCount >= maxRetry) {
            throw ServiceException("生成租户ID失败，请稍后重试")
        }

        return tenantId
    }

    override fun dynamicTenant(tenantId: String) {
        // 校验租户是否存在
        val tenant = jdbcTemplate.query(
            "SELECT tenant_id FROM sys_tenant WHERE tenant_id = ? AND del_flag = '0'",
            arrayOf(tenantId)
        ) { rs, _ -> rs.getString("tenant_id") }.firstOrNull()
            ?: throw ServiceException("租户不存在")
        TenantHelper.setDynamic(tenantId, true)
    }

    override fun clearDynamic() {
        TenantHelper.clearDynamic()
    }

    /**
     * ResultSet 转换为 VO
     */
    private fun entityToVo(rs: java.sql.ResultSet): SysTenantVo {
        // 查询套餐名称（需要join或单独查询）
        val packageId = rs.getLong("package_id")
        val packageName = if (!rs.wasNull()) {
            jdbcTemplate.queryForObject(
                "SELECT package_name FROM sys_tenant_package WHERE id = ?",
                String::class.java,
                packageId
            )
        } else null

        return SysTenantVo(
            tenantId = rs.getString("tenant_id"),
            contactUserName = rs.getString("contact_user_name"),
            contactPhone = rs.getString("contact_phone"),
            companyName = rs.getString("company_name"),
            packageId = if (!rs.wasNull()) packageId else null,
            packageName = packageName,
            expireTime = rs.getTimestamp("expire_time")?.toLocalDateTime(),
            accountCount = rs.getLong("account_count").takeIf { !rs.wasNull() },
            status = rs.getString("status"),
            remark = rs.getString("remark"),
            createTime = rs.getTimestamp("create_time")?.toLocalDateTime()
        )
    }
}
