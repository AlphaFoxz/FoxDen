package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysLogininforService
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.bo.SysLogininforBo
import com.github.alphafoxz.foxden.domain.system.vo.SysLogininforVo
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.*
import org.springframework.stereotype.Service

/**
 * Logininfor 业务层处理
 */
@Service
class SysLogininforServiceImpl(
    private val sqlClient: KSqlClient
) : SysLogininforService {

    override fun selectPageList(bo: SysLogininforBo, pageQuery: PageQuery): TableDataInfo<SysLogininforVo> {
        // TODO: Implement proper pagination when needed
        val logininfors = sqlClient.createQuery(SysLogininfor::class) {
            bo.infoId?.let { where(table.id eq it) }
            bo.userName?.takeIf { it.isNotBlank() }?.let { where(table.userName like "%${it}%") }
            bo.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }
            orderBy(table.id.desc())
            select(table)
        }.execute()

        return TableDataInfo.build(logininfors.map { entityToVo(it) })
    }

    override fun insertLogininfor(bo: SysLogininforBo): Int {
        val newLogininfor = com.github.alphafoxz.foxden.domain.system.entity.SysLogininforDraft.`$`.produce {
            tenantId = "default" // TODO: Get from context
            userName = bo.userName
            clientKey = null // TODO: Get from context
            deviceType = null // TODO: Get from request
            status = bo.status
            ipaddr = bo.ipaddr
            loginLocation = bo.loginLocation
            browser = bo.browser
            os = bo.os
            msg = bo.msg
            loginTime = java.util.Date()
        }

        val result = sqlClient.save(newLogininfor)
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
        // Delete all login records
        val allIds = sqlClient.createQuery(SysLogininfor::class) {
            select(table.id)
        }.execute()

        if (allIds.isNotEmpty()) {
            sqlClient.deleteByIds(SysLogininfor::class, allIds)
        }
    }

    /**
     * 实体转 VO
     */
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
