package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.excel.utils.ExcelUtil
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysLogininforBo
import com.github.alphafoxz.foxden.domain.system.service.SysLogininforService
import com.github.alphafoxz.foxden.domain.system.service.extensions.clean
import com.github.alphafoxz.foxden.domain.system.service.extensions.deleteByIds
import com.github.alphafoxz.foxden.domain.system.service.extensions.selectList
import com.github.alphafoxz.foxden.domain.system.vo.SysLogininforVo
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*

/**
 * 系统访问记录
 *
 * @author FoxDen Team
 */
@RestController
@RequestMapping("/monitor/logininfor")
class SysLogininforController(
    private val logininforService: SysLogininforService
) : BaseController() {

    /**
     * 获取登录日志列表
     */
    @SaCheckPermission("monitor:logininfor:list")
    @GetMapping("/list")
    fun list(bo: SysLogininforBo, pageQuery: PageQuery): TableDataInfo<SysLogininforVo> {
        return logininforService.selectPageList(bo, pageQuery)
    }

    /**
     * 导出登录日志
     */
    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @SaCheckPermission("monitor:logininfor:export")
    @PostMapping("/export")
    fun export(bo: SysLogininforBo, response: HttpServletResponse) {
        val list = logininforService.selectList(bo)
        ExcelUtil.exportExcel(list, "登录日志", SysLogininforVo::class.java, response)
    }

    /**
     * 删除登录日志
     */
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    fun remove(@PathVariable infoIds: Array<Long>): R<Void> {
        return toAjax(logininforService.deleteByIds(infoIds))
    }

    /**
     * 清空登录日志
     */
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    fun clean(): R<Void> {
        logininforService.clean()
        return R.ok()
    }
}
