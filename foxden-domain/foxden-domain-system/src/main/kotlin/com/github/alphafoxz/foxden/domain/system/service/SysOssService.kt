package com.github.alphafoxz.foxden.domain.system.service

import com.github.alphafoxz.foxden.common.oss.entity.UploadResult
import com.github.alphafoxz.foxden.domain.system.bo.SysOssBo
import com.github.alphafoxz.foxden.domain.system.vo.SysOssVo
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

/**
 * 文件上传 服务层
 *
 * @author Lion Li
 */
interface SysOssService {

    /**
     * 分页查询OSS对象存储列表
     *
     * @param bo OSS对象存储信息
     * @param pageQuery 分页参数
     * @return OSS对象存储分页列表
     */
    fun queryPageList(bo: SysOssBo, pageQuery: com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery): com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo<SysOssVo>

    /**
     * 根据ID查询OSS对象存储
     *
     * @param ossId OSS对象存储ID
     * @return OSS对象存储
     */
    fun queryById(ossId: Long): SysOssVo?

    /**
     * 上传文件到OSS
     *
     * @param file 文件
     * @return 结果
     */
    fun upload(file: MultipartFile): SysOssVo?

    /**
     * 上传文件到OSS
     *
     * @param fileName 文件名称
     * @param content 文件内容
     * @return 结果
     */
    fun upload(fileName: String, content: InputStream): SysOssVo?

    /**
     * 上传文件到OSS
     *
     * @param fileName 文件名称
     * @param content  文件内容
     * @return 结果
     */
    fun upload(fileName: String, content: ByteArray): SysOssVo?

    /**
     * 通过OSS对象存储ID查询文件
     *
     * @param ossId OSS对象存储ID
     * @return 文件路径
     */
    fun selectByIds(ossId: Long): SysOssVo?

    /**
     * 删除OSS对象存储
     *
     * @param ossIds OSS对象存储ID
     * @return 结果
     */
    fun deleteWithValidByIds(ossIds: Array<Long>): Int

    /**
     * 获取签名
     *
     * @param ossId OSS对象ID
     * @return 签名字符串
     */
    fun getPresignedObjectUrl(ossId: Long): String?
}
