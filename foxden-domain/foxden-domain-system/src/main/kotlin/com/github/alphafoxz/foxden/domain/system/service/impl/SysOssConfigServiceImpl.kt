package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.common.core.constant.CacheNames
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
import com.github.alphafoxz.foxden.common.oss.constant.OssConstant
import com.github.alphafoxz.foxden.common.oss.properties.OssProperties
import com.github.alphafoxz.foxden.common.redis.utils.CacheUtils
import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import com.github.alphafoxz.foxden.domain.system.bo.SysOssConfigBo
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.service.SysOssConfigService
import com.github.alphafoxz.foxden.domain.system.service.extensions.saveWithAutoId
import com.github.alphafoxz.foxden.domain.system.vo.SysOssConfigVo
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.like
import org.babyfish.jimmer.sql.kt.ast.expression.ne
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 对象存储配置Service业务层处理
 */
@Service
class SysOssConfigServiceImpl(
    private val sqlClient: KSqlClient
) : SysOssConfigService {

    private val log = LoggerFactory.getLogger(SysOssConfigServiceImpl::class.java)

    /**
     * 项目启动时，初始化参数到缓存，加载配置类
     */
    override fun init() {
        val list = sqlClient.createQuery(SysOssConfig::class) {
            orderBy(table.id.asc())
            select(table)
        }.execute()

        // 加载OSS初始化配置
        for (config in list) {
            val configKey = config.configKey
            if ("0" == config.status) {
                RedisUtils.setCacheObject(OssConstant.DEFAULT_CONFIG_KEY, configKey)
            }
            // 将配置转换为 OssProperties 并缓存
            val properties = toOssProperties(config)
            CacheUtils.put(
                CacheNames.SYS_OSS_CONFIG,
                configKey,
                JsonUtils.toJsonString(properties)
            )
        }
    }

    override fun queryById(ossConfigId: Long): SysOssConfigVo? {
        val config = sqlClient.findById(SysOssConfig::class, ossConfigId) ?: return null
        return entityToVo(config)
    }

    override fun queryPageList(bo: SysOssConfigBo, pageQuery: PageQuery): TableDataInfo<SysOssConfigVo> {
        val configs = sqlClient.createQuery(SysOssConfig::class) {
            bo.ossConfigId?.let { where(table.id eq it) }
            bo.configKey?.takeIf { it.isNotBlank() }?.let { where(table.configKey eq it) }
            bo.bucketName?.takeIf { it.isNotBlank() }?.let { where(table.bucketName like "%${it}%") }
            bo.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }
            orderBy(table.id.asc())
            select(table)
        }.execute()

        return TableDataInfo.build(configs.map { entityToVo(it) })
    }

    override fun insertByBo(bo: SysOssConfigBo): Boolean {
        // 校验配置key唯一性
        if (StringUtils.isNotEmpty(bo.configKey) && !checkConfigKeyUnique(bo)) {
            throw ServiceException("操作配置'${bo.configKey}'失败, 配置key已存在!")
        }

        val newConfig = SysOssConfigDraft.`$`.produce {
            configKey = bo.configKey ?: throw ServiceException("配置key不能为空")
            accessKey = bo.accessKey
            secretKey = bo.secretKey
            bucketName = bo.bucketName
            prefix = bo.prefix
            endpoint = bo.endpoint
            domain = if (bo.isDomain == true) bo.endpoint else null
            httpsFlag = if (bo.isHttps == true) "1" else "0"
            region = bo.region
            status = bo.status ?: SystemConstants.NORMAL
            ext1 = bo.ext
            remark = bo.remark
            accessPolicy = bo.accessPolicy
            createTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.saveWithAutoId(newConfig)
        if (result.isModified) {
            // 从数据库查询完整的数据做缓存
            val config = sqlClient.findById(SysOssConfig::class, newConfig.id)
            if (config != null) {
                val properties = toOssProperties(config)
                CacheUtils.put(
                    CacheNames.SYS_OSS_CONFIG,
                    config.configKey,
                    JsonUtils.toJsonString(properties)
                )
            }
            return true
        }
        return false
    }

    override fun updateByBo(bo: SysOssConfigBo): Boolean {
        val ossConfigId = bo.ossConfigId ?: return false

        // 校验配置key唯一性
        if (StringUtils.isNotEmpty(bo.configKey) && !checkConfigKeyUnique(bo)) {
            throw ServiceException("操作配置'${bo.configKey}'失败, 配置key已存在!")
        }

        val existing = sqlClient.findById(SysOssConfig::class, ossConfigId)
            ?: throw ServiceException("OSS配置不存在")

        val updated = SysOssConfigDraft.`$`.produce(existing) {
            bo.configKey?.let { configKey = it }
            bo.accessKey?.let { accessKey = it }
            bo.secretKey?.let { secretKey = it }
            bo.bucketName?.let { bucketName = it }
            // null 值需要设置为空字符串（与老版本保持一致）
            prefix = if (bo.prefix != null) bo.prefix else ""
            bo.endpoint?.let { endpoint = it }
            if (bo.isDomain == true) {
                bo.endpoint?.let { domain = it }
            }
            bo.isHttps?.let { httpsFlag = if (it) "1" else "0" }
            // null 值需要设置为空字符串（与老版本保持一致）
            region = if (bo.region != null) bo.region else ""
            // null 值需要设置为空字符串（与老版本保持一致）
            ext1 = if (bo.ext != null) bo.ext else ""
            // null 值需要设置为空字符串（与老版本保持一致）
            remark = if (bo.remark != null) bo.remark else ""
            bo.status?.let { status = it }
            bo.accessPolicy?.let { accessPolicy = it }
            updateTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(updated)
        if (result.isModified) {
            // 从数据库查询完整的数据做缓存
            val config = sqlClient.findById(SysOssConfig::class, ossConfigId)
            if (config != null) {
                val properties = toOssProperties(config)
                CacheUtils.put(
                    CacheNames.SYS_OSS_CONFIG,
                    config.configKey,
                    JsonUtils.toJsonString(properties)
                )
            }
            return true
        }
        return false
    }

    override fun deleteWithValidByIds(ids: Collection<Long>, isValid: Boolean): Boolean {
        if (isValid) {
            // 校验是否为系统数据
            val systemDataIds = OssConstant.SYSTEM_DATA_IDS
            if (ids.any { it in systemDataIds }) {
                throw ServiceException("系统内置, 不可删除!")
            }
        }

        // 获取要删除的配置
        val configs = sqlClient.findByIds(SysOssConfig::class, ids.toList())

        // 删除缓存
        for (config in configs) {
            CacheUtils.evict(CacheNames.SYS_OSS_CONFIG, config.configKey)
        }

        // 删除数据库记录
        val result = sqlClient.deleteByIds(SysOssConfig::class, ids.toList())
        return result.totalAffectedRowCount > 0
    }

    override fun updateOssConfigStatus(bo: SysOssConfigBo): Int {
        // 先将所有配置状态设置为1（停用）
        sqlClient.createUpdate(SysOssConfig::class) {
            set(table.status, "1")
            where(table.status ne "1")
        }.execute()

        // 更新指定配置的状态
        val ossConfigId = bo.ossConfigId ?: return 0
        val config = sqlClient.findById(SysOssConfig::class, ossConfigId) ?: return 0

        val updated = SysOssConfigDraft.`$`.produce(config) {
            status = "0"
        }

        val result = sqlClient.save(updated)
        if (result.isModified) {
            RedisUtils.setCacheObject(OssConstant.DEFAULT_CONFIG_KEY, config.configKey)
            return 1
        }
        return 0
    }

    /**
     * 判断configKey是否唯一
     */
    private fun checkConfigKeyUnique(bo: SysOssConfigBo): Boolean {
        val ossConfigId = bo.ossConfigId ?: -1L
        val configKey = bo.configKey ?: return false

        val existing = sqlClient.createQuery(SysOssConfig::class) {
            where(table.configKey eq configKey)
            where(table.id ne ossConfigId)
            select(table.id)
        }.fetchOneOrNull()

        return existing == null
    }

    /**
     * 实体转 VO
     */
    private fun entityToVo(config: SysOssConfig): SysOssConfigVo {
        return SysOssConfigVo(
            ossConfigId = config.id,
            configName = config.configKey,
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

    /**
     * 实体转 OssProperties
     */
    private fun toOssProperties(config: SysOssConfig): OssProperties {
        return OssProperties(
            endpoint = config.endpoint,
            domain = config.domain,
            prefix = config.prefix,
            accessKey = config.accessKey,
            secretKey = config.secretKey,
            bucketName = config.bucketName,
            region = config.region,
            isHttps = config.httpsFlag,
            accessPolicy = config.accessPolicy
        )
    }
}
