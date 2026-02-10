package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.common.core.constant.CacheNames
import com.github.alphafoxz.foxden.common.core.domain.dto.OssDTO
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.MapstructUtils
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.core.utils.file.FileUtils
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.oss.core.OssClient
import com.github.alphafoxz.foxden.common.oss.entity.UploadResult
import com.github.alphafoxz.foxden.common.oss.enums.AccessPolicyType
import com.github.alphafoxz.foxden.common.oss.factory.OssFactory
import com.github.alphafoxz.foxden.common.redis.utils.CacheUtils
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.service.SysOssService
import com.github.alphafoxz.foxden.domain.system.vo.SysOssVo
import jakarta.servlet.http.HttpServletResponse
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.time.Duration

/**
 * 文件上传 服务层实现
 *
 * @author Lion Li
 */
@Service
class SysOssServiceImpl(
    private val sqlClient: KSqlClient
) : SysOssService {

    /**
     * 查询OSS对象存储列表
     */
    override fun queryPageList(sysOss: com.github.alphafoxz.foxden.domain.system.bo.SysOssBo, pageQuery: PageQuery): TableDataInfo<SysOssVo> {
        val ossList = sqlClient.createQuery(SysOss::class) {
            sysOss.ossId?.let { where(table.id eq it) }
            sysOss.fileName?.takeIf { it.isNotBlank() }?.let { where(table.fileName like "%${it}%") }
            sysOss.originalName?.takeIf { it.isNotBlank() }?.let { where(table.originalName like "%${it}%") }
            sysOss.fileSuffix?.takeIf { it.isNotBlank() }?.let { where(table.fileSuffix eq it) }
            sysOss.service?.takeIf { it.isNotBlank() }?.let { where(table.service eq it) }
            sysOss.createBy?.takeIf { it.isNotBlank() }?.let { where(table.createBy eq it.toLong()) }
            orderBy(table.id.asc())
            select(table)
        }.execute()

        // 转换为 VO 并匹配 URL
        val voList = ossList.map { matchingUrl(entityToVo(it)) }
        return TableDataInfo.build(voList)
    }

    /**
     * 根据一组 ossIds 获取对应的 SysOssVo 列表
     */
    override fun listByIds(ossIds: Collection<Long>): List<SysOssVo> {
        val list = mutableListOf<SysOssVo>()
        val ossService = SpringUtils.getAopProxy(this)
        for (id in ossIds) {
            val vo = ossService.getById(id)
            if (vo != null) {
                try {
                    list.add(matchingUrl(vo))
                } catch (e: Exception) {
                    // 如果 oss 异常无法连接则将数据直接返回
                    list.add(vo)
                }
            }
        }
        return list
    }

    /**
     * 根据 ossId 从缓存或数据库中获取 SysOssVo 对象
     */
    @Cacheable(cacheNames = [CacheNames.SYS_OSS], key = "#ossId")
    override fun getById(ossId: Long): SysOssVo? {
        val oss = sqlClient.findById(SysOss::class, ossId) ?: return null
        return entityToVo(oss)
    }

    /**
     * 上传 MultipartFile 到对象存储服务
     */
    override fun upload(file: MultipartFile): SysOssVo {
        if (file.isEmpty) {
            throw ServiceException("上传文件不能为空")
        }
        val originalFileName = file.originalFilename ?: throw ServiceException("上传文件名不能为空")
        val suffix = if (originalFileName.contains(".")) {
            originalFileName.substring(originalFileName.lastIndexOf("."))
        } else {
            ""
        }

        val storage = OssFactory.instance()
        val uploadResult: UploadResult = try {
            storage.uploadSuffix(file.bytes, suffix, file.contentType)
        } catch (e: Exception) {
            throw ServiceException("上传文件失败: ${e.message}")
        }

        // 构建扩展信息
        val extInfo = mapOf(
            "fileSize" to file.size,
            "contentType" to (file.contentType ?: "")
        )

        // 保存文件信息到数据库
        val newOss = SysOssDraft.`$`.produce {
            this.fileName = uploadResult.filename ?: ""
            this.originalName = originalFileName
            this.fileSuffix = suffix
            this.url = uploadResult.url ?: ""
            this.service = storage.configKey ?: ""
            this.ext1 = JsonUtils.toJsonString(extInfo)
            this.createTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(newOss)
        if (!result.isModified) {
            throw ServiceException("保存文件信息失败")
        }

        // 获取保存后的实体并转换为 VO
        val savedOss = sqlClient.findById(SysOss::class, newOss.id) ?: throw ServiceException("保存文件信息失败")
        val vo = entityToVo(savedOss)
        return matchingUrl(vo)
    }

    /**
     * 上传文件到对象存储服务
     */
    override fun upload(file: File): SysOssVo {
        if (!file.isFile || file.length() <= 0) {
            throw ServiceException("上传文件不能为空")
        }
        val originalFileName = file.name
        val suffix = if (originalFileName.contains(".")) {
            originalFileName.substring(originalFileName.lastIndexOf("."))
        } else {
            ""
        }

        val storage = OssFactory.instance()
        val uploadResult = storage.uploadSuffix(file, suffix)

        // 构建扩展信息
        val extInfo = mapOf(
            "fileSize" to file.length()
        )

        // 保存文件信息到数据库
        val newOss = SysOssDraft.`$`.produce {
            this.fileName = uploadResult.filename ?: ""
            this.originalName = originalFileName
            this.fileSuffix = suffix
            this.url = uploadResult.url ?: ""
            this.service = storage.configKey ?: ""
            this.ext1 = JsonUtils.toJsonString(extInfo)
            this.createTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(newOss)
        if (!result.isModified) {
            throw ServiceException("保存文件信息失败")
        }

        val savedOss = sqlClient.findById(SysOss::class, newOss.id) ?: throw ServiceException("保存文件信息失败")
        val vo = entityToVo(savedOss)
        return matchingUrl(vo)
    }

    /**
     * 文件下载
     */
    override fun download(ossId: Long, response: HttpServletResponse) {
        val sysOss = SpringUtils.getAopProxy(this).getById(ossId)
            ?: throw ServiceException("文件数据不存在!")

        FileUtils.setAttachmentResponseHeader(response, sysOss.originalName ?: "download")
        response.contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=UTF-8"

        val storage = OssFactory.instance(sysOss.service ?: throw ServiceException("OSS配置不存在"))
        val fileName = sysOss.fileName ?: throw ServiceException("文件名不存在")

        // 使用 OssClient 下载文件
        val inputStream = storage.getObjectContent(fileName)
        inputStream.use {
            response.setContentLength(inputStream.available())
            inputStream.copyTo(response.outputStream)
        }
    }

    /**
     * 根据一组 ossIds 获取对应文件的 URL 列表
     */
    override fun selectUrlByIds(ossIds: String): String {
        val list = mutableListOf<String>()
        val ossService = SpringUtils.getAopProxy(this)
        val ids = ossIds.split(",").map { it.trim().toLong() }
        for (id in ids) {
            val vo = ossService.getById(id)
            if (vo != null) {
                try {
                    list.add(matchingUrl(vo).url ?: "")
                } catch (e: Exception) {
                    // 如果 oss 异常无法连接则将数据直接返回
                    list.add(vo.url ?: "")
                }
            }
        }
        return list.joinToString(",")
    }

    /**
     * OssService 接口方法实现
     */
    override fun selectByIds(ossIds: String): List<OssDTO> {
        val list = mutableListOf<OssDTO>()
        val ossService = SpringUtils.getAopProxy(this)
        val ids = ossIds.split(",").map { it.trim().toLong() }
        for (id in ids) {
            val vo = ossService.getById(id)
            if (vo != null) {
                try {
                    vo.url = matchingUrl(vo).url
                    list.add(MapstructUtils.convert(vo, OssDTO::class.java) ?: continue)
                } catch (e: Exception) {
                    // 如果 oss 异常无法连接则将数据直接返回
                    list.add(MapstructUtils.convert(vo, OssDTO::class.java) ?: continue)
                }
            }
        }
        return list
    }

    /**
     * 删除OSS对象存储
     */
    override fun deleteWithValidByIds(ids: Collection<Long>, isValid: Boolean): Boolean {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }

        // 查询要删除的 OSS 记录
        val ossList = sqlClient.findByIds(SysOss::class, ids.toList())

        // 删除云存储中的文件
        for (sysOss in ossList) {
            try {
                val storage = OssFactory.instance(sysOss.service ?: continue)
                val url = sysOss.url ?: continue
                storage.delete(url)
            } catch (e: Exception) {
                // 忽略删除失败
            }
        }

        // 删除数据库记录
        val result = sqlClient.deleteByIds(SysOss::class, ids.toList())
        return result.totalAffectedRowCount > 0
    }

    /**
     * 桶类型为 private 的URL 修改为临时URL时长为120s
     */
    private fun matchingUrl(oss: SysOssVo): SysOssVo {
        val storage = OssFactory.instance(oss.service ?: return oss)
        // 仅修改桶类型为 private 的URL，临时URL时长为120s
        if (AccessPolicyType.PRIVATE == storage.getAccessPolicy()) {
            val fileName = oss.fileName ?: return oss
            oss.url = storage.getPrivateUrl(fileName, Duration.ofSeconds(120))
        }
        return oss
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
            createBy = oss.createBy?.toString(),
            createTime = oss.createTime
        )
    }
}
