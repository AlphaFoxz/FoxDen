package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysNoticeBo
import com.github.alphafoxz.foxden.domain.system.service.SysNoticeService
import com.github.alphafoxz.foxden.domain.system.service.extensions.selectPageNoticeList
import com.github.alphafoxz.foxden.domain.system.vo.SysNoticeVo
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 通知公告
 *
 * @author FoxDen Team
 */
@Validated
@RestController
@RequestMapping("/system/notice")
class SysNoticeController(
    private val noticeService: SysNoticeService
) : BaseController() {

    /**
     * 获取通知公告列表
     */
    @SaCheckPermission("system:notice:list")
    @GetMapping("/list")
    fun list(notice: SysNoticeBo, pageQuery: PageQuery): TableDataInfo<SysNoticeVo> {
        return noticeService.selectPageNoticeList(notice, pageQuery)
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @SaCheckPermission("system:notice:query")
    @GetMapping("/{noticeId}")
    fun getInfo(@PathVariable noticeId: Long): R<SysNoticeVo> {
        return R.ok(noticeService.selectNoticeById(noticeId))
    }

    /**
     * 新增通知公告
     */
    @SaCheckPermission("system:notice:add")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(@Validated @RequestBody notice: SysNoticeBo): R<Void> {
        return toAjax(noticeService.insertNotice(notice))
    }

    /**
     * 修改通知公告
     */
    @SaCheckPermission("system:notice:edit")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(@Validated @RequestBody notice: SysNoticeBo): R<Void> {
        return toAjax(noticeService.updateNotice(notice))
    }

    /**
     * 删除通知公告
     */
    @SaCheckPermission("system:notice:remove")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    fun remove(@PathVariable noticeIds: Array<Long>): R<Void> {
        noticeService.deleteNoticeByIds(noticeIds)
        return R.ok()
    }
}
