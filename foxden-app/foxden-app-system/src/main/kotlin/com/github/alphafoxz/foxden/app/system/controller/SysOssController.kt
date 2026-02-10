package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.validate.QueryGroup
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysOssBo
import com.github.alphafoxz.foxden.domain.system.service.SysOssService
import com.github.alphafoxz.foxden.domain.system.vo.SysOssUploadVo
import com.github.alphafoxz.foxden.domain.system.vo.SysOssVo
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.NotEmpty
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * 文件上传 控制层
 *
 * @author FoxDen Team
 */
@Validated
@RestController
@RequestMapping("/resource/oss")
class SysOssController(
    private val ossService: SysOssService
) : BaseController() {

    /**
     * 查询OSS对象存储列表
     */
    @SaCheckPermission("system:oss:list")
    @GetMapping("/list")
    fun list(@Validated(QueryGroup::class) bo: SysOssBo, pageQuery: PageQuery): TableDataInfo<SysOssVo> {
        return ossService.queryPageList(bo, pageQuery)
    }

    /**
     * 查询OSS对象基于id串
     *
     * @param ossIds OSS对象ID串
     */
    @SaCheckPermission("system:oss:query")
    @GetMapping("/listByIds/{ossIds}")
    fun listByIds(@NotEmpty(message = "主键不能为空") @PathVariable ossIds: Array<Long>): R<List<SysOssVo>> {
        val list = ossService.listByIds(ossIds.toList())
        return R.ok(list)
    }

    /**
     * 上传OSS对象存储
     *
     * @param file 文件
     */
    @SaCheckPermission("system:oss:upload")
    @Log(title = "OSS对象存储", businessType = BusinessType.INSERT)
    @PostMapping(value = ["/upload"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(@RequestPart("file") file: MultipartFile): R<SysOssUploadVo> {
        val oss = ossService.upload(file)
        val uploadVo = SysOssUploadVo(
            url = oss.url,
            fileName = oss.fileName,
            ossId = oss.ossId.toString()
        )
        return R.ok(uploadVo)
    }

    /**
     * 下载OSS对象
     *
     * @param ossId OSS对象ID
     */
    @SaCheckPermission("system:oss:download")
    @GetMapping("/download/{ossId}")
    @Throws(Exception::class)
    fun download(@PathVariable ossId: Long, response: HttpServletResponse) {
        ossService.download(ossId, response)
    }

    /**
     * 删除OSS对象存储
     *
     * @param ossIds OSS对象ID串
     */
    @SaCheckPermission("system:oss:remove")
    @Log(title = "OSS对象存储", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ossIds}")
    fun remove(@NotEmpty(message = "主键不能为空") @PathVariable ossIds: Array<Long>): R<Void> {
        return toAjax(ossService.deleteWithValidByIds(ossIds.toList(), true))
    }
}
