package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysOssConfigService
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.bo.SysOssConfigBo
import com.github.alphafoxz.foxden.domain.system.vo.SysOssConfigVo
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.*
import org.springframework.stereotype.Service

/**
 * OssConfig 业务层处理
 */
@Service
class SysOssConfigServiceImpl(
    private val sqlClient: KSqlClient
) : SysOssConfigService {

    override fun selectOssConfigList(bo: SysOssConfigBo): List<SysOssConfigVo> {
        val configs = sqlClient.createQuery(SysOssConfig::class) {
            bo.ossConfigId?.let { where(table.id eq it) }
            bo.configKey?.takeIf { it.isNotBlank() }?.let { where(table.configKey eq it) }
            bo.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }
            orderBy(table.id.asc())
            select(table)
        }.execute()

        return configs.map { entityToVo(it) }
    }

    override fun selectOssConfigById(ossConfigId: Long): SysOssConfigVo? {
        val config = sqlClient.findById(SysOssConfig::class, ossConfigId) ?: return null
        return entityToVo(config)
    }

    override fun insertOssConfig(bo: SysOssConfigBo): Int {
        val newConfig = com.github.alphafoxz.foxden.domain.system.entity.SysOssConfigDraft.`$`.produce {
            configKey = bo.configKey ?: throw ServiceException("配置key不能为空")
            accessKey = bo.accessKey
            secretKey = bo.secretKey
            bucketName = bo.bucketName
            prefix = bo.prefix
            endpoint = bo.endpoint
            domain = bo.endpoint // Use endpoint as domain if isDomain is true
            httpsFlag = if (bo.isHttps == true) "1" else "0"
            region = bo.region
            status = bo.status ?: SystemConstants.NORMAL
            ext1 = bo.ext
            remark = bo.remark
            createTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(newConfig)
        return if (result.isModified) 1 else 0
    }

    override fun updateOssConfig(bo: SysOssConfigBo): Int {
        val ossConfigIdVal = bo.ossConfigId ?: return 0
        val existing = sqlClient.findById(SysOssConfig::class, ossConfigIdVal)
            ?: throw ServiceException("OSS配置不存在")

        val updated = com.github.alphafoxz.foxden.domain.system.entity.SysOssConfigDraft.`$`.produce(existing) {
            bo.configKey?.let { configKey = it }
            bo.accessKey?.let { accessKey = it }
            bo.secretKey?.let { secretKey = it }
            bo.bucketName?.let { bucketName = it }
            bo.prefix?.let { prefix = it }
            bo.endpoint?.let { endpoint = it }
            bo.endpoint?.let { domain = it }
            bo.isHttps?.let { httpsFlag = if (it) "1" else "0" }
            bo.region?.let { region = it }
            bo.status?.let { status = it }
            bo.ext?.let { ext1 = it }
            bo.remark?.let { remark = it }
            updateTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(updated)
        return if (result.isModified) 1 else 0
    }

    override fun deleteOssConfigByIds(ossConfigIds: Array<Long>) {
        sqlClient.deleteByIds(SysOssConfig::class, ossConfigIds.toList())
    }

    override fun checkConfigKeyUnique(bo: SysOssConfigBo): Boolean {
        val existing = sqlClient.createQuery(SysOssConfig::class) {
            where(table.configKey eq bo.configKey)
            bo.ossConfigId?.let { where(table.id ne it) }
            select(table)
        }.fetchOneOrNull()

        return existing == null || existing.id == bo.ossConfigId
    }

    /**
     * 实体转 VO
     * Note: BO/VO have configName, isDomain, isHttps, ext fields that entity doesn't have
     * Entity uses httpsFlag, ext1 instead
     */
    private fun entityToVo(config: SysOssConfig): SysOssConfigVo {
        return SysOssConfigVo(
            ossConfigId = config.id,
            configName = config.configKey, // Use configKey as configName
            configKey = config.configKey,
            accessKey = config.accessKey,
            secretKey = config.secretKey,
            bucketName = config.bucketName,
            prefix = config.prefix,
            endpoint = config.endpoint,
            isDomain = config.domain?.isNotEmpty(),
            isHttps = config.httpsFlag == "1",
            region = config.region,
            status = config.status,
            ext = config.ext1,
            remark = config.remark,
            createTime = config.createTime
        )
    }
}
