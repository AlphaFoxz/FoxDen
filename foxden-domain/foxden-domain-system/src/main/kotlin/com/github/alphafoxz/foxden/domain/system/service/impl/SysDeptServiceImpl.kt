package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysDeptService
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.bo.SysDeptBo
import com.github.alphafoxz.foxden.domain.system.vo.SysDeptVo
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

/**
 * Dept 业务层处理
 */
@Service
class SysDeptServiceImpl(
    private val sqlClient: KSqlClient,
    private val jdbcTemplate: JdbcTemplate
) : SysDeptService {

    override fun selectDeptList(dept: SysDeptBo): List<SysDeptVo> {
        val depts = sqlClient.createQuery(SysDept::class) {
            where(table.delFlag eq "0")
            dept.deptId?.let { where(table.id eq it) }
            dept.deptName?.takeIf { it.isNotBlank() }?.let { where(table.deptName like "%${it}%") }
            dept.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }
            orderBy(table.orderNum.asc())
            select(table)
        }.execute()

        return depts.map { entityToVo(it) }
    }

    override fun selectDeptById(deptId: Long): SysDeptVo? {
        val dept = sqlClient.findById(SysDept::class, deptId) ?: return null
        return entityToVo(dept)
    }

    override fun selectNormalChildrenDeptById(deptId: Long): Int {
        val count = sqlClient.createQuery(SysDept::class) {
            where(table.parentId eq deptId)
            where(table.status eq SystemConstants.NORMAL)
            where(table.delFlag eq "0")
            select(table.id)
        }.execute().count()
        return count
    }

    override fun hasChildByDeptId(deptId: Long): Boolean {
        val count = sqlClient.createQuery(SysDept::class) {
            where(table.parentId eq deptId)
            where(table.delFlag eq "0")
            select(table.id)
        }.execute().count()
        return count > 0
    }

    override fun checkDeptExistUser(deptId: Long): Boolean {
        val count = sqlClient.createQuery(SysUser::class) {
            where(table.deptId eq deptId)
            where(table.delFlag eq "0")
            select(table.id)
        }.execute().count()
        return count > 0
    }

    override fun checkDeptNameUnique(dept: SysDeptBo): Boolean {
        val existing = sqlClient.createQuery(SysDept::class) {
            where(table.deptName eq dept.deptName)
            dept.deptId?.let { where(table.id ne it) }
            where(table.parentId eq (dept.parentId ?: 0L))
            select(table)
        }.fetchOneOrNull()

        return existing == null || existing.id == dept.deptId
    }

    override fun checkDeptDataScope(deptId: Long) {
        // TODO: 实现数据权限检查
    }

    override fun insertDept(bo: SysDeptBo): Int {
        val newDept = com.github.alphafoxz.foxden.domain.system.entity.SysDeptDraft.`$`.produce {
            parentId = bo.parentId
            deptName = bo.deptName ?: throw ServiceException("部门名称不能为空")
            deptCategory = bo.deptCategory
            orderNum = bo.orderNum ?: 0
            leader = bo.leader
            phone = bo.phone
            email = bo.email
            status = bo.status ?: SystemConstants.NORMAL
            delFlag = "0"
            createTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(newDept)
        return if (result.isModified) 1 else 0
    }

    override fun updateDept(bo: SysDeptBo): Int {
        val deptIdVal = bo.deptId ?: return 0
        val existing = sqlClient.findById(SysDept::class, deptIdVal)
            ?: throw ServiceException("部门不存在")

        val updated = com.github.alphafoxz.foxden.domain.system.entity.SysDeptDraft.`$`.produce(existing) {
            bo.parentId?.let { parentId = it }
            bo.deptName?.let { deptName = it }
            bo.deptCategory?.let { deptCategory = it }
            bo.orderNum?.let { orderNum = it }
            bo.leader?.let { leader = it }
            bo.phone?.let { phone = it }
            bo.email?.let { email = it }
            bo.status?.let { status = it }
            updateTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(updated)
        return if (result.isModified) 1 else 0
    }

    override fun deleteDeptById(deptId: Long): Int {
        val result = sqlClient.deleteById(SysDept::class, deptId)
        return result.totalAffectedRowCount.toInt()
    }

    override fun buildDeptTreeSelect(depts: List<SysDeptVo>): List<com.github.alphafoxz.foxden.common.core.domain.Tree<Long>> {
        if (depts.isEmpty()) {
            return emptyList()
        }

        return com.github.alphafoxz.foxden.common.core.utils.TreeBuildUtils.buildMultiRoot(
            depts,
            { it.deptId!! },
            { it.parentId ?: 0L },
            { node, treeNode ->
                treeNode.setId(node.deptId!!)
                    .setParentId(node.parentId)
                    .setName(node.deptName!!)
                    .setWeight(node.orderNum ?: 0)
                    .putExtra("disabled", SystemConstants.DISABLE == node.status)
            }
        )
    }

    override fun selectDeptListByRoleId(roleId: Long): List<Long> {
        // 查询角色信息
        val role = sqlClient.findById(SysRole::class, roleId) ?: return emptyList()

        // 根据角色的部门检查严格性来查询部门ID
        val deptIds = if (role.deptCheckStrictly == true) {
            // 严格模式：只查询直接关联的部门
            jdbcTemplate.queryForList(
                "SELECT dept_id FROM sys_role_dept WHERE role_id = ?",
                Long::class.java,
                roleId
            )
        } else {
            // 非严格模式：查询直接关联的部门及其子部门
            val directDeptIds = jdbcTemplate.queryForList(
                "SELECT dept_id FROM sys_role_dept WHERE role_id = ?",
                Long::class.java,
                roleId
            )

            if (directDeptIds.isEmpty()) {
                return emptyList()
            }

            // 查询所有子部门
            val allDeptIds = mutableListOf<Long>()
            directDeptIds.forEach { deptId ->
                allDeptIds.add(deptId)
                // 递归查询子部门
                findAllChildDeptIds(deptId, allDeptIds)
            }
            allDeptIds
        }

        return deptIds
    }

    override fun selectDeptTreeList(dept: SysDeptBo): List<com.github.alphafoxz.foxden.common.core.domain.Tree<Long>> {
        val depts = sqlClient.createQuery(SysDept::class) {
            where(table.delFlag eq "0")
            dept.deptId?.let { where(table.id eq it) }
            dept.parentId?.let { where(table.parentId eq it) }
            dept.deptName?.takeIf { it.isNotBlank() }?.let { where(table.deptName like "%${it}%") }
            dept.deptCategory?.takeIf { it.isNotBlank() }?.let { where(table.deptCategory eq it) }
            dept.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }
            orderBy(
                table.ancestors.asc(),
                table.parentId.asc(),
                table.orderNum.asc(),
                table.id.asc()
            )
            select(table)
        }.execute()

        return buildDeptTreeSelect(depts.map { entityToVo(it) })
    }

    /**
     * 递归查询所有子部门ID
     */
    private fun findAllChildDeptIds(parentId: Long, result: MutableList<Long>) {
        val childIds = jdbcTemplate.queryForList(
            "SELECT dept_id FROM sys_dept WHERE parent_id = ? AND del_flag = '0'",
            Long::class.java,
            parentId
        )

        childIds.forEach { childId ->
            if (!result.contains(childId)) {
                result.add(childId)
                findAllChildDeptIds(childId, result)
            }
        }
    }

    /**
     * 实体转 VO
     */
    private fun entityToVo(dept: SysDept): SysDeptVo {
        return SysDeptVo(
            deptId = dept.id,
            parentId = dept.parentId,
            ancestors = dept.ancestors,
            deptName = dept.deptName,
            deptCategory = dept.deptCategory,
            orderNum = dept.orderNum,
            leader = dept.leader,
            phone = dept.phone,
            email = dept.email,
            status = dept.status,
            delFlag = dept.delFlag.toString(),
            createTime = dept.createTime
        )
    }
}
