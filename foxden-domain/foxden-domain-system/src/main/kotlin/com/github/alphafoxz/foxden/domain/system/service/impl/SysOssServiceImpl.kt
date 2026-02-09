package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysOssService
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.bo.SysOssBo
import com.github.alphafoxz.foxden.domain.system.vo.SysOssVo
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.*
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

/**
 * Oss 业务层处理
 */
@Service
class SysOssServiceImpl(
    private val sqlClient: KSqlClient
) : SysOssService {

    override fun queryPageList(bo: SysOssBo, pageQuery: PageQuery): TableDataInfo<SysOssVo> {
        // TODO: Implement proper pagination when needed
        val ossList = sqlClient.createQuery(SysOss::class) {
            bo.ossId?.let { where(table.id eq it) }
            bo.fileName?.takeIf { it.isNotBlank() }?.let { where(table.fileName like "%${it}%") }
            bo.originalName?.takeIf { it.isNotBlank() }?.let { where(table.originalName like "%${it}%") }
            bo.service?.takeIf { it.isNotBlank() }?.let { where(table.service eq it) }
            orderBy(table.id.desc())
            select(table)
        }.execute()

        return TableDataInfo.build(ossList.map { entityToVo(it) })
    }

    override fun queryById(ossId: Long): SysOssVo? {
        val oss = sqlClient.findById(SysOss::class, ossId) ?: return null
        return entityToVo(oss)
    }

    override fun upload(file: MultipartFile): SysOssVo? {
        // TODO: Implement file upload when OSS client is available
        // This requires integration with OSS service providers (MinIO, Aliyun OSS, etc.)
        return null
    }

    override fun upload(fileName: String, content: InputStream): SysOssVo? {
        // TODO: Implement file upload when OSS client is available
        // This requires integration with OSS service providers (MinIO, Aliyun OSS, etc.)
        return null
    }

    override fun upload(fileName: String, content: ByteArray): SysOssVo? {
        // TODO: Implement file upload when OSS client is available
        // This requires integration with OSS service providers (MinIO, Aliyun OSS, etc.)
        return null
    }

    override fun selectByIds(ossId: Long): SysOssVo? {
        return queryById(ossId)
    }

    override fun deleteWithValidByIds(ossIds: Array<Long>): Int {
        var deletedCount = 0
        ossIds.forEach { ossId ->
            val result = sqlClient.deleteById(SysOss::class, ossId)
            deletedCount += result.totalAffectedRowCount.toInt()
        }
        return deletedCount
    }

    override fun getPresignedObjectUrl(ossId: Long): String? {
        val oss = sqlClient.findById(SysOss::class, ossId) ?: return null
        // TODO: Generate presigned URL when OSS client is available
        return oss.url
    }

    /**
     * 实体转 VO
     */
    private fun entityToVo(oss: SysOss): SysOssVo {
        return SysOssVo(
            ossId = oss.id,
            fileName = oss.fileName,
            originalName = oss.originalName,
            fileSuffix = oss.fileSuffix,
            url = oss.url,
            service = oss.service,
            createTime = oss.createTime
            // Note: createBy in entity is Long (user ID), but VO expects String (username)
            // For now, skip setting this field
        )
    }
}
