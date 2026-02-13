package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysOperLogBo
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.service.SysOperLogService
import com.github.alphafoxz.foxden.domain.system.service.extensions.saveWithAutoId
import com.github.alphafoxz.foxden.domain.system.vo.SysOperLogVo
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.like
import org.springframework.stereotype.Service

/**
 * OperLog 业务层处理
 */
@Service
class SysOperLogServiceImpl(
    private val sqlClient: KSqlClient
) : SysOperLogService {

    override fun selectPageOperLogList(bo: SysOperLogBo, pageQuery: PageQuery): TableDataInfo<SysOperLogVo> {
        // TODO: Implement proper pagination when needed
        val operLogs = sqlClient.createQuery(SysOperLog::class) {
            bo.operId?.let { where(table.id eq it) }
            bo.title?.takeIf { it.isNotBlank() }?.let { where(table.title like "%${it}%") }
            bo.businessType?.let { where(table.businessType eq it) }
            bo.status?.let { where(table.status eq it) }
            bo.operName?.takeIf { it.isNotBlank() }?.let { where(table.operName like "%${it}%") }
            orderBy(table.operTime.desc())
            select(table)
        }.execute()

        return TableDataInfo.build(operLogs.map { entityToVo(it) })
    }

    override fun insertOperlog(bo: SysOperLogBo) {
        val newOperLog = com.github.alphafoxz.foxden.domain.system.entity.SysOperLogDraft.`$`.produce {
            tenantId = "default" // TODO: Get from context
            title = bo.title
            businessType = bo.businessType
            method = bo.method
            requestMethod = bo.requestMethod
            operatorType = bo.operatorType
            operName = bo.operName
            deptName = bo.deptName
            operUrl = bo.operUrl
            operIp = bo.operIp
            operLocation = bo.operLocation
            operParam = bo.operParam
            jsonResult = bo.jsonResult
            status = bo.status
            errorMsg = bo.errorMsg
            operTime = bo.operTime?.let {
                java.util.Date.from(it.atZone(java.time.ZoneId.systemDefault()).toInstant())
            }
            costTime = bo.costTime
        }

        sqlClient.saveWithAutoId(newOperLog)
    }

    override fun selectOperLogById(operId: Long): SysOperLogVo? {
        val operLog = sqlClient.findById(SysOperLog::class, operId) ?: return null
        return entityToVo(operLog)
    }

    override fun deleteOperLogByIds(operIds: Array<Long>) {
        sqlClient.deleteByIds(SysOperLog::class, operIds.toList())
    }

    override fun cleanOperLog() {
        // Delete all oper log records
        val allIds = sqlClient.createQuery(SysOperLog::class) {
            select(table.id)
        }.execute()

        if (allIds.isNotEmpty()) {
            sqlClient.deleteByIds(SysOperLog::class, allIds)
        }
    }

    /**
     * 实体转 VO
     */
    private fun entityToVo(operLog: SysOperLog): SysOperLogVo {
        return SysOperLogVo(
            operId = operLog.id,
            title = operLog.title,
            businessType = operLog.businessType,
            method = operLog.method,
            requestMethod = operLog.requestMethod,
            operatorType = operLog.operatorType,
            operName = operLog.operName,
            deptName = operLog.deptName,
            operUrl = operLog.operUrl,
            operIp = operLog.operIp,
            operLocation = operLog.operLocation,
            operParam = operLog.operParam,
            jsonResult = operLog.jsonResult,
            status = operLog.status,
            errorMsg = operLog.errorMsg,
            operTime = operLog.operTime?.let {
                java.time.LocalDateTime.ofInstant(
                    it.toInstant(),
                    java.time.ZoneId.systemDefault()
                )
            },
            costTime = operLog.costTime
        )
    }
}
