package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysClientService
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.bo.SysClientBo
import com.github.alphafoxz.foxden.domain.system.vo.SysClientVo
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.*
import org.springframework.stereotype.Service

/**
 * Client 业务层处理
 */
@Service
class SysClientServiceImpl(
    private val sqlClient: KSqlClient
) : SysClientService {

    override fun selectClientList(bo: SysClientBo): List<SysClientVo> {
        val clients = sqlClient.createQuery(SysClient::class) {
            where(table.delFlag eq "0")
            bo.id?.let { where(table.id eq it) }
            bo.clientId?.takeIf { it.isNotBlank() }?.let { where(table.clientId eq it) }
            bo.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }
            orderBy(table.id.asc())
            select(table)
        }.execute()

        return clients.map { entityToVo(it) }
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
        val newClient = com.github.alphafoxz.foxden.domain.system.entity.SysClientDraft.`$`.produce {
            clientId = bo.clientId ?: throw ServiceException("客户端ID不能为空")
            clientKey = bo.clientKey ?: throw ServiceException("客户端密钥不能为空")
            grantType = bo.grantType
            deviceType = bo.clientType // Map clientType to deviceType
            status = bo.status ?: SystemConstants.NORMAL
            delFlag = "0"
            createTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(newClient)
        return if (result.isModified) 1 else 0
    }

    override fun updateClient(bo: SysClientBo): Int {
        val idVal = bo.id ?: return 0
        val existing = sqlClient.findById(SysClient::class, idVal)
            ?: throw ServiceException("客户端信息不存在")

        val updated = com.github.alphafoxz.foxden.domain.system.entity.SysClientDraft.`$`.produce(existing) {
            bo.clientId?.let { clientId = it }
            bo.clientKey?.let { clientKey = it }
            bo.grantType?.let { grantType = it }
            bo.clientType?.let { deviceType = it } // Map clientType to deviceType
            bo.status?.let { status = it }
            updateTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(updated)
        return if (result.isModified) 1 else 0
    }

    override fun checkClientIdUnique(bo: SysClientBo): Boolean {
        val existing = sqlClient.createQuery(SysClient::class) {
            where(table.clientId eq bo.clientId)
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
