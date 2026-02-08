package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysDeptService
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.bo.SysDeptBo
import com.github.alphafoxz.foxden.domain.system.vo.SysDeptVo
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.*
import org.springframework.stereotype.Service

/**
 * Dept 业务层处理
 */
@Service
class SysDeptServiceImpl(
    private val sqlClient: KSqlClient
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

    /**
     * 实体转 VO
     */
    private fun entityToVo(dept: SysDept): SysDeptVo {
        return SysDeptVo(
            deptId = dept.id,
            parentId = dept.parentId,
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
