package com.github.alphafoxz.foxden.domain.system.service.impl

import cn.hutool.crypto.SecureUtil
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysClientBo
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.service.SysClientService
import com.github.alphafoxz.foxden.domain.system.service.extensions.saveWithAutoId
import com.github.alphafoxz.foxden.domain.system.vo.SysClientVo
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.ne
import org.springframework.stereotype.Service

/**
 * Client 业务层处理
 */
@Service
class SysClientServiceImpl(
    private val sqlClient: KSqlClient
) : SysClientService {

    override fun selectClientList(bo: SysClientBo, pageQuery: PageQuery): TableDataInfo<SysClientVo> {
        val pager = sqlClient.createQuery(SysClient::class) {
            where(table.delFlag eq "0")
            bo.id?.let { where(table.id eq it) }
            bo.clientId?.takeIf { it.isNotBlank() }?.let { where(table.clientId eq it) }
            bo.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }
            orderBy(table.id.asc())
            select(table)
        }.fetchPage(
            pageQuery.getPageNumOrDefault() - 1,
            pageQuery.getPageSizeOrDefault()
        )

        val voList = pager.rows.map { entityToVo(it) }
        return TableDataInfo(voList, pager.totalRowCount)
    }

    override fun selectClientById(id: Long): SysClientVo? {
        val client = sqlClient.findById(SysClient::class, id) ?: return null
        return entityToVo(client)
    }

    override fun queryByClientId(clientId: String): SysClientVo? {
        val client = sqlClient.createQuery(SysClient::class) {
            where(table.clientId eq clientId)
            where(table.delFlag eq "0")
            select(table)
        }.fetchOneOrNull()

        return client?.let { entityToVo(it) }
    }

    override fun insertClient(bo: SysClientBo): Int {
        val cliKey = bo.clientKey ?: throw ServiceException("客户端密钥不能为空")
        val cliSecret = bo.clientSecret ?: throw ServiceException("客户端秘钥不能为空")

        // 生成clientId：MD5(clientKey + clientSecret)
        val generatedClientId = SecureUtil.md5(cliKey + cliSecret)

        // 将grantTypeList转换为逗号分隔的字符串
        val grantTypeStr = if (!bo.grantTypeList.isNullOrEmpty()) {
            bo.grantTypeList!!.joinToString(StringUtils.SEPARATOR)
        } else {
            bo.grantType
        }

        val newClient = SysClientDraft.`$`.produce {
            clientId = generatedClientId
            clientKey = cliKey
            clientSecret = cliSecret
            grantType = grantTypeStr
            deviceType = bo.clientType
            activeTimeout = bo.activeTimeout
            timeout = bo.timeout
            status = bo.status ?: SystemConstants.NORMAL
            delFlag = "0"
            createTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.saveWithAutoId(newClient)
        return if (result.isModified) 1 else 0
    }

    override fun updateClient(bo: SysClientBo): Int {
        val idVal = bo.id ?: return 0
        val existing = sqlClient.findById(SysClient::class, idVal)
            ?: throw ServiceException("客户端信息不存在")

        // Use createUpdate instead of save to avoid upsert behavior
        // and get accurate affected row count
        val rows = sqlClient.createUpdate(SysClient::class) {
            where(table.id eq idVal)
            bo.clientId?.let { set(table.clientId, it) }
            bo.clientKey?.let { set(table.clientKey, it) }
            bo.grantType?.let { set(table.grantType, it) }
            bo.clientType?.let { set(table.deviceType, it) } // Map clientType to deviceType
            bo.status?.let { set(table.status, it) }
            set(table.updateTime, java.time.LocalDateTime.now())
        }.execute()

        return rows
    }

    override fun checkClientKeyUnique(bo: SysClientBo): Boolean {
        val existing = sqlClient.createQuery(SysClient::class) {
            bo.clientKey?.takeIf { it.isNotBlank() }?.let { where(table.clientKey eq it) }
            where(table.delFlag eq "0")
            bo.id?.let { where(table.id ne it) }
            select(table)
        }.fetchOneOrNull()

        return existing == null || existing.id == bo.id
    }

    override fun deleteClientById(id: Long) {
        val existing = sqlClient.findById(SysClient::class, id) ?: return
        val updated = com.github.alphafoxz.foxden.domain.system.entity.SysClientDraft.`$`.produce(existing) {
            delFlag = "1"
        }
        sqlClient.save(updated)
    }

    /**
     * 实体转 VO
     * Note: Entity is missing some fields that exist in BO/VO (clientName, clientType, redirectUri, autoApprove)
     * These will need to be added to the entity in a future update
     */
    private fun entityToVo(client: SysClient): SysClientVo {
        return SysClientVo(
            id = client.id,
            clientId = client.clientId,
            clientKey = client.clientKey,
            grantType = client.grantType,
            status = client.status,
            deviceType = client.deviceType,
            activeTimeout = client.activeTimeout,
            timeout = client.timeout,
            remark = null,  // SysClient table doesn't have remark column
            createTime = client.createTime
            // Note: clientName, clientType, redirectUri, autoApprove don't exist in entity
            // clientType field in VO maps to deviceType in entity
        )
    }
}
