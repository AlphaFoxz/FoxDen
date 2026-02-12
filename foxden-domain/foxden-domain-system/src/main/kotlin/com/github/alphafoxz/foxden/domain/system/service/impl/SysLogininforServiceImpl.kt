package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysLogininforBo
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.service.SysLogininforService
import com.github.alphafoxz.foxden.domain.system.vo.SysLogininforVo
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.between
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.like
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
class SysLogininforServiceImpl(
    private val sqlClient: KSqlClient
) : SysLogininforService {

    override fun selectPageList(bo: SysLogininforBo, pageQuery: PageQuery): TableDataInfo<SysLogininforVo> {
        // 使用 Jimmer DSL 查询
        val pager = sqlClient.createQuery(SysLogininfor::class) {
            // IP地址模糊查询
            bo.ipaddr?.takeIf { it.isNotBlank() }?.let {
                where(table.ipaddr like "%$it%")
            }
            // 状态精确查询
            bo.status?.takeIf { it.isNotBlank() }?.let {
                where(table.status eq it)
            }
            // 用户名模糊查询
            bo.userName?.takeIf { it.isNotBlank() }?.let {
                where(table.userName like "%$it%")
            }

            // 时间范围查询
            val beginTime = bo.beginTime
            val endTime = bo.endTime
            if (beginTime != null && endTime != null) {
                val beginDate = Timestamp.valueOf(beginTime)
                val endDate = Timestamp.valueOf(endTime)
                where(table.loginTime.between(beginDate, endDate))
            }

            // 排序
            if (pageQuery.orderByColumn.isNullOrBlank()) {
                orderBy(table.id.desc())
            }
            select(table)
        }.fetchPage(pageQuery.getPageNumOrDefault() - 1, pageQuery.getPageSizeOrDefault())

        return TableDataInfo(pager.rows.map { entityToVo(it) }, pager.totalRowCount)
    }

    override fun selectList(bo: SysLogininforBo): List<SysLogininforVo> {
        // 使用 Jimmer DSL 查询（参照老项目 selectLogininforList）
        val logininfors = sqlClient.createQuery(SysLogininfor::class) {
            // IP地址模糊查询
            bo.ipaddr?.takeIf { it.isNotBlank() }?.let {
                where(table.ipaddr like "%$it%")
            }
            // 状态精确查询
            bo.status?.takeIf { it.isNotBlank() }?.let {
                where(table.status eq it)
            }
            // 用户名模糊查询
            bo.userName?.takeIf { it.isNotBlank() }?.let {
                where(table.userName like "%$it%")
            }
            // 时间范围查询
            val beginTime = bo.beginTime
            val endTime = bo.endTime
            if (beginTime != null && endTime != null) {
                val beginDate = Timestamp.valueOf(beginTime)
                val endDate = Timestamp.valueOf(endTime)
                where(table.loginTime.between(beginDate, endDate))
            }
            // 按ID倒序排序
            orderBy(table.id.desc())
            select(table)
        }.execute()

        return logininfors.map { entityToVo(it) }
    }

    override fun insertLogininfor(bo: SysLogininforBo): Int {
        val newLogininfor = SysLogininforDraft.`$`.produce {
            tenantId = bo.tenantId ?: "default"
            userName = bo.userName
            clientKey = bo.clientKey
            deviceType = bo.deviceType
            status = bo.status
            ipaddr = bo.ipaddr
            loginLocation = bo.loginLocation
            browser = bo.browser
            os = bo.os
            msg = bo.msg
            loginTime = java.util.Date()
        }

        // 使用 INSERT_ONLY 模式，因为登录日志始终是插入新记录
        val result = sqlClient.save(newLogininfor, SaveMode.INSERT_ONLY)
        return if (result.isModified) 1 else 0
    }

    override fun selectLogininforById(infoId: Long): SysLogininforVo? {
        val logininfor = sqlClient.findById(SysLogininfor::class, infoId) ?: return null
        return entityToVo(logininfor)
    }

    override fun deleteLogininforByIds(infoIds: Array<Long>) {
        sqlClient.deleteByIds(SysLogininfor::class, infoIds.toList())
    }

    override fun cleanLogininfor() {
        val allIds = sqlClient.createQuery(SysLogininfor::class) {
            select(table.id)
        }.execute()

        if (allIds.isNotEmpty()) {
            sqlClient.deleteByIds(SysLogininfor::class, allIds)
        }
    }

    private fun entityToVo(logininfor: SysLogininfor): SysLogininforVo {
        return SysLogininforVo(
            infoId = logininfor.id,
            userName = logininfor.userName,
            ipaddr = logininfor.ipaddr,
            loginLocation = logininfor.loginLocation,
            browser = logininfor.browser,
            os = logininfor.os,
            status = logininfor.status,
            msg = logininfor.msg,
            loginTime = logininfor.loginTime?.let {
                java.time.LocalDateTime.ofInstant(
                    it.toInstant(),
                    java.time.ZoneId.systemDefault()
                )
            }
        )
    }
}