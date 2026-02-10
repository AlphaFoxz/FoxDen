package com.github.alphafoxz.foxden.domain.system.service

import com.github.alphafoxz.foxden.common.core.domain.dto.OssDTO
import com.github.alphafoxz.foxden.common.core.service.OssService
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysOssBo
import com.github.alphafoxz.foxden.domain.system.vo.SysOssVo
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.multipart.MultipartFile
import java.io.File

/**
 * 文件上传 服务层
 *
 * @author Lion Li
 */
interface SysOssService : OssService {

    /**
     * 查询OSS对象存储列表
     *
     * @param sysOss    OSS对象存储分页查询对象
     * @param pageQuery 分页查询实体类
     * @return 结果
     */
    fun queryPageList(sysOss: SysOssBo, pageQuery: PageQuery): TableDataInfo<SysOssVo>

    /**
     * 根据一组 ossIds 获取对应的 SysOssVo 列表
     *
     * @param ossIds 一组文件在数据库中的唯一标识集合
     * @return 包含 SysOssVo 对象的列表
     */
    fun listByIds(ossIds: Collection<Long>): List<SysOssVo>

    /**
     * 根据 ossId 从缓存或数据库中获取 SysOssVo 对象
     *
     * @param ossId 文件在数据库中的唯一标识
     * @return SysOssVo 对象，包含文件信息
     */
    fun getById(ossId: Long): SysOssVo?

    /**
     * 上传 MultipartFile 到对象存储服务，并保存文件信息到数据库
     *
     * @param file 要上传的 MultipartFile 对象
     * @return 上传成功后的 SysOssVo 对象，包含文件信息
     */
    fun upload(file: MultipartFile): SysOssVo

    /**
     * 上传文件到对象存储服务，并保存文件信息到数据库
     *
     * @param file 要上传的文件对象
     * @return 上传成功后的 SysOssVo 对象，包含文件信息
     */
    fun upload(file: File): SysOssVo

    /**
     * 文件下载方法，支持一次性下载完整文件
     *
     * @param ossId    OSS对象ID
     * @param response HttpServletResponse对象，用于设置响应头和向客户端发送文件内容
     */
    @Throws(Exception::class)
    fun download(ossId: Long, response: HttpServletResponse)

    /**
     * 根据一组 ossIds 获取对应文件的 URL 列表
     *
     * @param ossIds 以逗号分隔的 ossId 字符串
     * @return 以逗号分隔的文件 URL 字符串
     */
    fun selectUrlByIds(ossIds: String): String

    /**
     * OssService 接口方法实现 - 根据ossIds获取DTO列表
     */
    override fun selectByIds(ossIds: String): List<OssDTO>

    /**
     * 删除OSS对象存储
     *
     * @param ids     OSS对象ID串
     * @param isValid 判断是否需要校验
     * @return 结果
     */
    fun deleteWithValidByIds(ids: Collection<Long>, isValid: Boolean): Boolean
}
