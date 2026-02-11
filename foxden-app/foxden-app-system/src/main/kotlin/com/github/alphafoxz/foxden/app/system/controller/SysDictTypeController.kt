package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.excel.utils.ExcelUtil
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysDictTypeBo
import com.github.alphafoxz.foxden.domain.system.service.SysDictTypeService
import com.github.alphafoxz.foxden.domain.system.vo.SysDictTypeVo
import jakarta.servlet.http.HttpServletResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 字典类型控制器
 *
 * @author FoxDen Team
 */
@Validated
@RestController
@RequestMapping("/system/dict/type")
class SysDictTypeController(
    private val dictTypeService: SysDictTypeService
) : BaseController() {

    /**
     * 查询字典类型列表
     */
    @SaCheckPermission("system:dict:list")
    @GetMapping("/list")
    fun list(dictType: SysDictTypeBo, pageQuery: PageQuery): TableDataInfo<SysDictTypeVo> {
        return dictTypeService.selectPageDictTypeList(dictType, pageQuery)
    }

    /**
     * 导出字典类型列表
     */
    @Log(title = "字典类型", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:dict:export")
    @PostMapping("/export")
    fun export(dictType: SysDictTypeBo, response: HttpServletResponse) {
        val list = dictTypeService.selectDictTypeList(dictType)
        ExcelUtil.exportExcel(list, "字典类型", SysDictTypeVo::class.java, response)
    }

    /**
     * 根据字典类型ID查询详细信息
     */
    @SaCheckPermission("system:dict:query")
    @GetMapping("/{dictId}")
    fun getInfo(@PathVariable dictId: Long): R<SysDictTypeVo> {
        return R.ok(dictTypeService.selectDictTypeById(dictId))
    }

    /**
     * 新增字典类型
     */
    @SaCheckPermission("system:dict:add")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(@Validated @RequestBody dictType: SysDictTypeBo): R<Void> {
        if (!dictTypeService.checkDictTypeUnique(dictType)) {
            return R.fail("新增字典'" + dictType.dictName + "'失败，字典类型已存在")
        }
        dictTypeService.insertDictType(dictType)
        return R.ok()
    }

    /**
     * 修改字典类型
     */
    @SaCheckPermission("system:dict:edit")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(@Validated @RequestBody dictType: SysDictTypeBo): R<Void> {
        if (!dictTypeService.checkDictTypeUnique(dictType)) {
            return R.fail("修改字典'" + dictType.dictName + "'失败，字典类型已存在")
        }
        dictTypeService.updateDictType(dictType)
        return R.ok()
    }

    /**
     * 删除字典类型
     */
    @SaCheckPermission("system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictIds}")
    fun remove(@PathVariable dictIds: Array<Long>): R<Void> {
        dictTypeService.deleteDictTypeByIds(dictIds)
        return R.ok()
    }

    /**
     * 刷新字典缓存
     */
    @SaCheckPermission("system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    fun refreshCache(): R<Void> {
        dictTypeService.resetDictCache()
        return R.ok()
    }

    /**
     * 获取字典选择框列表
     */
    @SaCheckPermission("system:dict:query")
    @GetMapping("/optionselect")
    fun optionselect(): R<List<SysDictTypeVo>> {
        return R.ok(dictTypeService.selectDictTypeAll())
    }
}
