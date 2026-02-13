package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.excel.utils.ExcelUtil
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysOperLogBo
import com.github.alphafoxz.foxden.domain.system.service.SysOperLogService
import com.github.alphafoxz.foxden.domain.system.service.extensions.clean
import com.github.alphafoxz.foxden.domain.system.service.extensions.deleteByIds
import com.github.alphafoxz.foxden.domain.system.service.extensions.selectById
import com.github.alphafoxz.foxden.domain.system.service.extensions.selectList
import com.github.alphafoxz.foxden.domain.system.vo.SysOperLogVo
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*

/**
 * 操作日志记录
 *
 * @author FoxDen Team
 */
@RestController
@RequestMapping("/monitor/operlog")
class SysOperLogController(
    private val operLogService: SysOperLogService
) : BaseController() {

    /**
     * 获取操作日志列表
     */
    @SaCheckPermission("monitor:operlog:list")
    @GetMapping("/list")
    fun list(bo: SysOperLogBo, pageQuery: PageQuery): TableDataInfo<SysOperLogVo> {
        return operLogService.selectPageOperLogList(bo, pageQuery)
    }

    /**
     * 导出操作日志
     */
    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @SaCheckPermission("monitor:operlog:export")
    @PostMapping("/export")
    fun export(bo: SysOperLogBo, response: HttpServletResponse) {
        val list = operLogService.selectList(bo)
        ExcelUtil.exportExcel(list, "操作日志", SysOperLogVo::class.java, response)
    }

    /**
     * 根据操作编号获取详细信息
     */
    @SaCheckPermission("monitor:operlog:query")
    @GetMapping("/{operId}")
    fun getInfo(@PathVariable operId: Long): R<SysOperLogVo> {
        return R.ok(operLogService.selectById(operId))
    }

    /**
     * 删除操作日志
     */
    @SaCheckPermission("monitor:operlog:remove")
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{operIds}")
    fun remove(@PathVariable operIds: Array<Long>): R<Void> {
        return toAjax(operLogService.deleteByIds(operIds))
    }

    /**
     * 清空操作日志
     */
    @SaCheckPermission("monitor:operlog:remove")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    fun clean(): R<Void> {
        operLogService.clean()
        return R.ok()
    }
}
