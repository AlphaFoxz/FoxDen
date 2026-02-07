package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.excel.utils.ExcelUtil
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysDictDataBo
import com.github.alphafoxz.foxden.domain.system.service.SysDictDataService
import com.github.alphafoxz.foxden.domain.system.service.SysDictTypeService
import com.github.alphafoxz.foxden.domain.system.vo.SysDictDataVo
import jakarta.servlet.http.HttpServletResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 字典数据控制器
 *
 * @author FoxDen Team
 */
@Validated
@RestController
@RequestMapping("/system/dict/data")
class SysDictDataController(
    private val dictDataService: SysDictDataService,
    private val dictTypeService: SysDictTypeService
) : BaseController() {

    /**
     * 查询字典数据列表
     */
    @SaCheckPermission("system:dict:list")
    @GetMapping("/list")
    fun list(dictData: SysDictDataBo): R<List<SysDictDataVo>> {
        return R.ok(dictDataService.selectDictDataList(dictData))
    }

    /**
     * 导出字典数据列表
     */
    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:dict:export")
    @PostMapping("/export")
    fun export(dictData: SysDictDataBo, response: HttpServletResponse) {
        val list = dictDataService.selectDictDataList(dictData)
        ExcelUtil.exportExcel(list, "字典数据", SysDictDataVo::class.java, response)
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping("/type/{dictType}")
    fun dictType(@PathVariable dictType: String): R<List<SysDictDataVo>> {
        return R.ok(dictDataService.selectDictDataByType(dictType))
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     */
    @GetMapping("/type/{dictType}/{dictValue}")
    fun dictLabel(
        @PathVariable dictType: String,
        @PathVariable dictValue: String
    ): R<String> {
        return R.ok(dictDataService.selectDictLabel(dictType, dictValue))
    }

    /**
     * 根据字典数据ID查询详细信息
     */
    @SaCheckPermission("system:dict:query")
    @GetMapping("/{dictCode}")
    fun getInfo(@PathVariable dictCode: Long): R<SysDictDataVo> {
        return R.ok(dictDataService.selectDictDataById(dictCode))
    }

    /**
     * 新增字典数据
     */
    @SaCheckPermission("system:dict:add")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(@Validated @RequestBody dictData: SysDictDataBo): R<Void> {
        if (!dictDataService.checkDictDataUnique(dictData)) {
            return R.fail("新增字典数据'" + dictData.dictLabel + "'失败，字典键值已存在")
        }
        return toAjax(dictDataService.insertDictData(dictData))
    }

    /**
     * 修改字典数据
     */
    @SaCheckPermission("system:dict:edit")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(@Validated @RequestBody dictData: SysDictDataBo): R<Void> {
        if (!dictDataService.checkDictDataUnique(dictData)) {
            return R.fail("修改字典数据'" + dictData.dictLabel + "'失败，字典键值已存在")
        }
        return toAjax(dictDataService.updateDictData(dictData))
    }

    /**
     * 删除字典数据
     */
    @SaCheckPermission("system:dict:remove")
    @Log(title = "字典数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    fun remove(@PathVariable dictCodes: Array<Long>): R<Void> {
        dictDataService.deleteDictDataByIds(dictCodes)
        return R.ok()
    }
}
