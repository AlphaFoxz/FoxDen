package com.github.alphafoxz.foxden.app.workflow.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.hutool.core.lang.tree.Tree
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.validate.AddGroup
import com.github.alphafoxz.foxden.common.core.validate.EditGroup
import com.github.alphafoxz.foxden.common.excel.utils.ExcelUtil
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowCategoryBo
import com.github.alphafoxz.foxden.domain.workflow.service.FlowCategoryService
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowCategoryVo
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 流程分类
 */
@Validated
@RestController
@RequestMapping("/workflow/category")
class FlowCategoryController(
    private val flowCategoryService: FlowCategoryService
) : BaseController() {

    /**
     * 查询流程分类列表
     */
    @SaCheckPermission("workflow:category:list")
    @GetMapping("/list")
    fun list(bo: FlowCategoryBo): R<List<FlowCategoryVo>> {
        val list = flowCategoryService.queryList(bo)
        return R.ok(list)
    }

    /**
     * 导出流程分类列表
     */
    @SaCheckPermission("workflow:category:export")
    @Log(title = "流程分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    fun export(bo: FlowCategoryBo, response: HttpServletResponse) {
        val list = flowCategoryService.queryList(bo)
        ExcelUtil.exportExcel(list, "流程分类", FlowCategoryVo::class.java, response)
    }

    /**
     * 获取流程分类详细信息
     *
     * @param categoryId 主键
     */
    @SaCheckPermission("workflow:category:query")
    @GetMapping("/{categoryId}")
    fun getInfo(
        @NotNull(message = "主键不能为空") @PathVariable categoryId: Long
    ): R<FlowCategoryVo?> {
        return R.ok(flowCategoryService.queryById(categoryId))
    }

    /**
     * 新增流程分类
     */
    @SaCheckPermission("workflow:category:add")
    @Log(title = "流程分类", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(
        @Validated(AddGroup::class) @RequestBody category: FlowCategoryBo
    ): R<Void> {
        if (!flowCategoryService.checkCategoryNameUnique(category)) {
            return R.fail("新增流程分类'" + category.categoryName + "'失败，流程分类名称已存在")
        }
        return toAjax(flowCategoryService.insertByBo(category))
    }

    /**
     * 修改流程分类
     */
    @SaCheckPermission("workflow:category:edit")
    @Log(title = "流程分类", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(
        @Validated(EditGroup::class) @RequestBody category: FlowCategoryBo
    ): R<Void> {
        val categoryId = category.categoryId
        if (!flowCategoryService.checkCategoryNameUnique(category)) {
            return R.fail("修改流程分类'" + category.categoryName + "'失败，流程分类名称已存在")
        } else if (category.parentId == categoryId) {
            return R.fail("修改流程分类'" + category.categoryName + "'失败，上级流程分类不能是自己")
        }
        return toAjax(flowCategoryService.updateByBo(category))
    }

    /**
     * 删除流程分类
     *
     * @param categoryId 主键
     */
    @SaCheckPermission("workflow:category:remove")
    @Log(title = "流程分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryId}")
    fun remove(@PathVariable categoryId: Long): R<Void> {
        if (categoryId == 1L) {
            return R.warn("默认流程分类,不允许删除")
        }
        if (flowCategoryService.hasChildByCategoryId(categoryId)) {
            return R.warn("存在下级流程分类,不允许删除")
        }
        if (flowCategoryService.checkCategoryExistDefinition(categoryId)) {
            return R.warn("流程分类存在流程定义,不允许删除")
        }
        return toAjax(flowCategoryService.deleteWithValidById(categoryId))
    }

    /**
     * 获取流程分类树列表
     *
     * @param categoryBo 流程分类
     */
    @GetMapping("/categoryTree")
    fun categoryTree(categoryBo: FlowCategoryBo): R<List<Tree<String>>> {
        return R.ok(flowCategoryService.selectCategoryTreeList(categoryBo))
    }
}
