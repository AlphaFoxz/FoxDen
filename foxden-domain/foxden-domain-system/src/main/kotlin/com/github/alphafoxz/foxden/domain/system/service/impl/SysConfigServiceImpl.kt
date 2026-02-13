package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.common.core.constant.CacheNames
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.redis.utils.CacheUtils
import com.github.alphafoxz.foxden.domain.system.bo.SysConfigBo
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.service.SysConfigService
import com.github.alphafoxz.foxden.domain.system.service.extensions.saveWithAutoId
import com.github.alphafoxz.foxden.domain.system.vo.SysConfigVo
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.like
import org.babyfish.jimmer.sql.kt.ast.expression.ne
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

/**
 * Config 业务层处理
 */
@Service
class SysConfigServiceImpl(
    private val sqlClient: KSqlClient
) : SysConfigService {

    override fun selectPageConfigList(config: SysConfigBo, pageQuery: PageQuery): TableDataInfo<SysConfigVo> {
        val configs = sqlClient.createQuery(SysConfig::class) {
            config.configId?.let { where(table.id eq it) }
            config.configName?.takeIf { it.isNotBlank() }?.let { where(table.configName like "%${it}%") }
            config.configKey?.takeIf { it.isNotBlank() }?.let { where(table.configKey like "%${it}%") }
            config.configType?.takeIf { it.isNotBlank() }?.let { where(table.configType eq it) }
            orderBy(table.id.asc())
            select(table)
        }.fetchPage((pageQuery.pageNum ?: 1) - 1, pageQuery.pageSize ?: 10)

        return TableDataInfo(configs.rows.map { entityToVo(it) }, configs.totalRowCount)
    }

    @Cacheable(cacheNames = [CacheNames.SYS_CONFIG], key = "#configKey")
    override fun selectConfigByKey(configKey: String): String? {
        val config = sqlClient.createQuery(SysConfig::class) {
            where(table.configKey eq configKey)
            select(table.configValue)
        }.fetchOneOrNull()

        return config
    }

    override fun selectConfigById(configId: Long): SysConfigVo {
        val config = sqlClient.findById(SysConfig::class, configId)
            ?: throw ServiceException("配置不存在")
        return entityToVo(config)
    }

    override fun selectRegisterEnabled(tenantId: String?): Boolean {
        // Default to true if config doesn't exist
        val configValue = selectConfigByKey("sys.account.registerUser")
        return configValue != "false"
    }

    override fun selectConfigObject(configKey: String): SysConfigVo? {
        val config = sqlClient.createQuery(SysConfig::class) {
            where(table.configKey eq configKey)
            select(table)
        }.fetchOneOrNull()

        return config?.let { entityToVo(it) }
    }

    override fun selectConfigValueByKey(configKey: String): String? {
        return selectConfigByKey(configKey)
    }

    override fun selectConfigValueByKey(configKey: String, defaultValue: String): String {
        return selectConfigByKey(configKey) ?: defaultValue
    }

    @CachePut(cacheNames = [CacheNames.SYS_CONFIG], key = "#bo.configKey")
    override fun insertConfig(bo: SysConfigBo): String {
        val newConfig = com.github.alphafoxz.foxden.domain.system.entity.SysConfigDraft.`$`.produce {
            configName = bo.configName ?: throw ServiceException("参数名称不能为空")
            configKey = bo.configKey ?: throw ServiceException("参数键名不能为空")
            configValue = bo.configValue
            configType = bo.configType ?: SystemConstants.NO
            remark = bo.remark
            createTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.saveWithAutoId(newConfig)
        if (!result.isModified) {
            throw ServiceException("操作失败")
        }
        return bo.configValue ?: throw ServiceException("参数值不能为空")
    }

    @CachePut(cacheNames = [CacheNames.SYS_CONFIG], key = "#bo.configKey")
    override fun updateConfig(bo: SysConfigBo): String {
        var row = 0

        // 路径1: 按 ID 更新
        if (bo.configId != null) {
            val temp = sqlClient.findById(SysConfig::class, bo.configId!!)
            if (temp != null && bo.configKey != null && temp.configKey != bo.configKey) {
                // 如果 configKey 发生变化，需要清除旧的缓存
                CacheUtils.evict(CacheNames.SYS_CONFIG, temp.configKey)
            }

            row = sqlClient.createUpdate(SysConfig::class) {
                where(table.id eq bo.configId!!)
                bo.configName?.let { set(table.configName, it) }
                bo.configKey?.let { set(table.configKey, it) }
                bo.configValue?.let { set(table.configValue, it) }
                bo.configType?.let { set(table.configType, it) }
                bo.remark?.let { set(table.remark, it) }
                set(table.updateTime, java.time.LocalDateTime.now())
            }.execute()
        }
        // 路径2: 按 configKey 更新（当 configId 为 null 时）
        else {
            // 先清除缓存
            bo.configKey?.let { CacheUtils.evict(CacheNames.SYS_CONFIG, it) }

            row = sqlClient.createUpdate(SysConfig::class) {
                where(table.configKey eq (bo.configKey ?: throw ServiceException("参数键名不能为空")))
                bo.configName?.let { set(table.configName, it) }
                bo.configValue?.let { set(table.configValue, it) }
                bo.configType?.let { set(table.configType, it) }
                bo.remark?.let { set(table.remark, it) }
                set(table.updateTime, java.time.LocalDateTime.now())
            }.execute()
        }

        if (row > 0) {
            return bo.configValue ?: throw ServiceException("参数值不能为空")
        }
        throw ServiceException("操作失败")
    }

    override fun deleteConfigByIds(configIds: Array<Long>): Int {
        sqlClient.deleteByIds(SysConfig::class, configIds.toList())
        return configIds.size
    }

    override fun resetConfigCache() {
        CacheUtils.clear(CacheNames.SYS_CONFIG)
    }

    override fun checkConfigKeyUnique(config: SysConfigBo): Boolean {
        val existing = sqlClient.createQuery(SysConfig::class) {
            where(table.configKey eq config.configKey)
            config.configId?.let { where(table.id ne it) }
            select(table)
        }.fetchOneOrNull()

        return existing == null || existing.id == config.configId
    }

    /**
     * 实体转 VO
     */
    private fun entityToVo(config: SysConfig): SysConfigVo {
        return SysConfigVo(
            configId = config.id,
            configName = config.configName,
            configKey = config.configKey,
            configValue = config.configValue,
            configType = config.configType,
            remark = config.remark,
            createTime = config.createTime
        )
    }
}
