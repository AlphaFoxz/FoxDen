package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysTenantPackageBo
import com.github.alphafoxz.foxden.domain.system.service.SysTenantPackageService
import com.github.alphafoxz.foxden.domain.system.service.extensions.saveWithAutoId
import com.github.alphafoxz.foxden.domain.system.vo.SysTenantPackageVo
import com.github.alphafoxz.foxden.domain.tenant.entity.*
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.like
import org.babyfish.jimmer.sql.kt.ast.expression.ne
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 租户套餐Service业务层处理
 *
 * @author Michelle.Chung
 */
@Service
class SysTenantPackageServiceImpl(
    private val sqlClient: KSqlClient,
    private val jdbcTemplate: JdbcTemplate
) : SysTenantPackageService {

    override fun selectTenantPackageList(
        bo: SysTenantPackageBo,
        pageQuery: PageQuery
    ): TableDataInfo<SysTenantPackageVo> {
        val pager = sqlClient.createQuery(SysTenantPackage::class) {
            where(table.delFlag eq "0")
            bo.packageId?.let { where(table.id eq it) }
            bo.packageName?.takeIf { it.isNotBlank() }?.let {
                where(table.packageName like "%${it}%")
            }
            bo.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }
            orderBy(table.id.asc())
            select(table)
        }.fetchPage(pageQuery.getPageNumOrDefault() - 1, pageQuery.getPageSizeOrDefault())

        return TableDataInfo(pager.rows.map { entityToVo(it) }, pager.totalRowCount)
    }

    override fun selectList(): List<SysTenantPackageVo> {
        val packages = sqlClient.createQuery(SysTenantPackage::class) {
            where(table.delFlag eq "0")
            where(table.status eq SystemConstants.NORMAL)
            orderBy(table.id.asc())
            select(table)
        }.execute()

        return packages.map { entityToVo(it) }
    }

    override fun selectTenantPackageById(packageId: Long): SysTenantPackageVo? {
        val tenantPackage = sqlClient.findById(SysTenantPackage::class, packageId) ?: return null
        return entityToVo(tenantPackage)
    }

    override fun checkPackageNameUnique(bo: SysTenantPackageBo): Boolean {
        val existing = sqlClient.createQuery(SysTenantPackage::class) {
            where(table.packageName eq bo.packageName)
            bo.packageId?.let { where(table.id ne it) }
            where(table.delFlag eq "0")
            select(table.id)
        }.fetchOneOrNull()

        return existing == null
    }

    @Transactional
    override fun insertTenantPackage(bo: SysTenantPackageBo): Int {
        // 检查套餐名称唯一性
        if (!checkPackageNameUnique(bo)) {
            throw ServiceException("套餐名称已存在")
        }

        // 保存菜单id
        val menuIdsStr = if (bo.menuIds.isNullOrEmpty()) "" else StringUtils.joinComma(bo.menuIds)

        val newPkg = SysTenantPackageDraft.`$`.produce {
            packageName = bo.packageName ?: throw ServiceException("套餐名称不能为空")
            this.menuIds = menuIdsStr
            menuCheckStrictly = false // 默认值
            remark = bo.remark
            status = bo.status ?: SystemConstants.NORMAL
            delFlag = "0"
            createTime = LocalDateTime.now()
            createBy = bo.createBy?.toLong()
        }

        val result = sqlClient.saveWithAutoId(newPkg)
        return if (result.isModified) 1 else 0
    }

    @Transactional
    override fun updateTenantPackage(bo: SysTenantPackageBo): Int {
        val packageId = bo.packageId ?: return 0

        // 检查套餐是否存在
        val existing = sqlClient.findById(SysTenantPackage::class, packageId)
            ?: throw ServiceException("租户套餐不存在")

        // 检查套餐名称唯一性
        if (!checkPackageNameUnique(bo)) {
            throw ServiceException("套餐名称已存在")
        }

        // 保存菜单id
        val menuIdsStr = if (bo.menuIds.isNullOrEmpty()) "" else StringUtils.joinComma(bo.menuIds)

        val updated = SysTenantPackageDraft.`$`.produce(existing) {
            bo.packageName?.let { packageName = it }
            this.menuIds = menuIdsStr
            bo.remark?.let { remark = it }
            bo.status?.let { status = it }
            updateTime = LocalDateTime.now()
            bo.createBy?.let { createBy = it.toLong() }
        }

        val result = sqlClient.save(updated)
        return result.totalAffectedRowCount
    }

    @Transactional
    override fun deleteTenantPackageById(packageId: Long) {
        // 检查套餐是否被租户使用
        val tenantExists = sqlClient.createQuery(SysTenant::class) {
            where(table.packageId eq packageId)
            where(table.delFlag eq "0")
            select(table.id)
        }.fetchOneOrNull()

        if (tenantExists != null) {
            throw ServiceException("租户套餐已被使用")
        }

        sqlClient.deleteById(SysTenantPackage::class, packageId)
    }

    @Transactional
    override fun deleteTenantPackageByIds(packageIds: Array<Long>) {
        if (packageIds.isEmpty()) return

        // 检查套餐是否被租户使用
        val tenantExists = packageIds.any { packageId ->
            sqlClient.createQuery(SysTenant::class) {
                where(table.packageId eq packageId)
                where(table.delFlag eq "0")
                select(table.id)
            }.fetchOneOrNull() != null
        }

        if (tenantExists) {
            throw ServiceException("租户套餐已被使用")
        }

        sqlClient.deleteByIds(SysTenantPackage::class, packageIds.toList())
    }

    override fun changeStatus(bo: SysTenantPackageBo): Int {
        val packageId = bo.packageId ?: return 0

        val rows = sqlClient.createUpdate(SysTenantPackage::class) {
            where(table.id eq packageId)
            set(table.status, bo.status ?: "")
            set(table.updateTime, LocalDateTime.now())
        }.execute()

        return rows
    }

    /**
     * 构建查询条件
     */
    private fun buildQueryWrapper(bo: SysTenantPackageBo) = sqlClient.createQuery(SysTenantPackage::class) {
        where(table.delFlag eq "0")
        bo.packageName?.takeIf { it.isNotBlank() }?.let {
            where(table.packageName like "%${it}%")
        }
        bo.status?.takeIf { it.isNotBlank() }?.let {
            where(table.status eq it)
        }
        orderBy(table.id.asc())
        select(table)
    }
}

/**
 * 实体转 VO
 */
private fun entityToVo(pkg: SysTenantPackage): SysTenantPackageVo {
    return SysTenantPackageVo(
        packageId = pkg.id,
        packageName = pkg.packageName,
        menuIds = pkg.menuIds,
        menuCheckStrictly = pkg.menuCheckStrictly,
        remark = pkg.remark,
        status = pkg.status,
        createTime = pkg.createTime
    )
}
