package com.github.alphafoxz.foxden.domain.system.service.impl

import cn.hutool.crypto.SecureUtil
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.jimmer.utils.AuditUtils
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
            bo.clientKey?.takeIf { it.isNotBlank() }?.let { where(table.clientKey eq it) }
            bo.clientSecret?.takeIf { it.isNotBlank() }?.let { where(table.clientSecret eq it) }
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

    override fun selectClientList(bo: SysClientBo): List<SysClientVo> {
        val list = sqlClient.createQuery(SysClient::class) {
            where(table.delFlag eq "0")
            bo.id?.let { where(table.id eq it) }
            bo.clientId?.takeIf { it.isNotBlank() }?.let { where(table.clientId eq it) }
            bo.clientKey?.takeIf { it.isNotBlank() }?.let { where(table.clientKey eq it) }
            bo.clientSecret?.takeIf { it.isNotBlank() }?.let { where(table.clientSecret eq it) }
            bo.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }
            orderBy(table.id.asc())
            select(table)
        }.execute()

        return list.map { entityToVo(it) }
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
        // 验证必填字段
        val cliKey = bo.clientKey ?: throw ServiceException("客户端密钥不能为空")
        val cliSecret = bo.clientSecret ?: throw ServiceException("客户端秘钥不能为空")

        // 生成clientId：MD5(clientKey + clientSecret)
        val generatedClientId = SecureUtil.md5(cliKey + cliSecret)

        // 将grantTypeList转换为逗号分隔的字符串（与老系统等价）
        val grantTypeStr = bo.grantTypeList?.joinToString(StringUtils.SEPARATOR)

        // 获取当前用户ID和部门ID用于审计字段（与老系统 InjectionMetaObjectHandler 逻辑等价）
        val currentUserId = AuditUtils.getCurrentUserId()
        val currentTime = AuditUtils.getCurrentTime()
        val currentDeptId =
            com.github.alphafoxz.foxden.common.security.utils.LoginHelper.getDeptId() ?: AuditUtils.DEFAULT_USER_ID

        val newClient = SysClientDraft.`$`.produce {
            clientId = generatedClientId
            clientKey = cliKey
            clientSecret = cliSecret
            grantType = grantTypeStr
            deviceType = bo.deviceType
            activeTimeout = bo.activeTimeout
            timeout = bo.timeout
            status = bo.status ?: SystemConstants.NORMAL
            delFlag = "0"

            // 审计字段（与老系统 MyBatis-Plus 自动填充逻辑等价）
            createBy = currentUserId
            updateBy = currentUserId
            createTime = currentTime
            updateTime = currentTime
            createDept = currentDeptId
        }

        val result = sqlClient.saveWithAutoId(newClient)
        return if (result.isModified) 1 else 0
    }

    override fun updateClient(bo: SysClientBo): Int {
        val idVal = bo.id ?: return 0

        // 获取当前用户ID和时间用于审计字段
        val currentUserId = AuditUtils.getCurrentUserId()
        val currentTime = AuditUtils.getCurrentTime()

        // 使用 createUpdate 进行更新，避免 upsert 行为并获取准确的受影响行数
        val rows = sqlClient.createUpdate(SysClient::class) {
            where(table.id eq idVal)
            bo.clientId?.let { set(table.clientId, it) }
            bo.clientKey?.let { set(table.clientKey, it) }
            bo.clientSecret?.let { set(table.clientSecret, it) }
            bo.grantType?.let { set(table.grantType, it) }
            bo.deviceType?.let { set(table.deviceType, it) }
            bo.activeTimeout?.let { set(table.activeTimeout, it) }
            bo.timeout?.let { set(table.timeout, it) }
            bo.status?.let { set(table.status, it) }
            set(table.updateBy, currentUserId)
            set(table.updateTime, currentTime)
        }.execute()

        return rows
    }

    override fun updateClientStatus(clientId: String, status: String): Int {
        val currentUserId = AuditUtils.getCurrentUserId()
        val currentTime = AuditUtils.getCurrentTime()

        return sqlClient.createUpdate(SysClient::class) {
            where(table.clientId eq clientId)
            set(table.status, status)
            set(table.updateBy, currentUserId)
            set(table.updateTime, currentTime)
        }.execute()
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
     * 将 grantType 字符串（逗号分隔）转换为 grantTypeList 列表
     */
    private fun entityToVo(client: SysClient): SysClientVo {
        val grantTypeList = client.grantType?.let {
            StringUtils.splitList(it)
        }

        return SysClientVo(
            id = client.id,
            clientId = client.clientId,
            clientKey = client.clientKey,
            clientSecret = client.clientSecret,
            grantType = client.grantType,
            grantTypeList = grantTypeList,
            status = client.status,
            deviceType = client.deviceType,
            activeTimeout = client.activeTimeout,
            timeout = client.timeout
        )
    }
}
