package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysDataScopeService
import com.github.alphafoxz.foxden.domain.system.entity.*
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Service

/**
 * DataScope 业务层处理
 */
@Service
class SysDataScopeServiceImpl(
    private val sqlClient: KSqlClient
) : SysDataScopeService {

    override fun selectDeptIdsByRoleId(roleId: Long): List<Long> {
        val role = sqlClient.findById(SysRole::class, roleId) ?: return emptyList()

        // Fetch role with departments using a specific query to get the departments
        val roleWithDepts = sqlClient.createQuery(SysRole::class) {
            where(table.id eq roleId)
            select(table)
        }.fetchOneOrNull() ?: return emptyList()

        // Get department IDs from the role's department associations
        // Note: This requires the role to have its departments loaded
        // For now, return empty list as the association loading needs further research
        return emptyList()
        // TODO: Research Jimmer's association loading to get role.departments
    }

    override fun selectDeptIdsByRoleIds(roleIds: Array<Long>): List<Long> {
        if (roleIds.isEmpty()) return emptyList()

        val allDeptIds = mutableListOf<Long>()
        roleIds.forEach { roleId ->
            val deptIds = selectDeptIdsByRoleId(roleId)
            allDeptIds.addAll(deptIds)
        }

        return allDeptIds.distinct()
    }
}
