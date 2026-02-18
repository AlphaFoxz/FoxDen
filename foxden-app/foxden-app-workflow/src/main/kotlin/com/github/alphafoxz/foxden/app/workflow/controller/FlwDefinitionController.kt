package com.github.alphafoxz.foxden.app.workflow.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.validate.AddGroup
import com.github.alphafoxz.foxden.common.core.validate.EditGroup
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowDefinitionBo
import com.github.alphafoxz.foxden.domain.workflow.service.FlowDefinitionService
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowDefinitionVo
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 流程定义Controller
 *
 * @author AprilWind
 */
@Validated
@RestController
@RequestMapping("/workflow/definition")
class FlwDefinitionController(
    private val flowDefinitionService: FlowDefinitionService
) : BaseController() {

    /**
     * 查询流程定义分页列表
     */
    @GetMapping("/list")
    fun list(flowDefinitionBo: FlowDefinitionBo, pageQuery: PageQuery): TableDataInfo<FlowDefinitionVo> {
        return flowDefinitionService.queryList(
            flowDefinitionBo.flowCode,
            flowDefinitionBo.flowName,
            pageQuery
        )
    }

    /**
     * 查询未发布的流程定义分页列表
     */
    @GetMapping("/unPublishList")
    fun unPublishList(flowDefinitionBo: FlowDefinitionBo, pageQuery: PageQuery): TableDataInfo<FlowDefinitionVo> {
        return flowDefinitionService.unPublishList(
            flowDefinitionBo.flowCode,
            flowDefinitionBo.flowName,
            pageQuery
        )
    }

    /**
     * 获取流程定义详细信息
     */
    @SaCheckPermission("workflow:definition:query")
    @GetMapping("/{id}")
    fun getInfo(@PathVariable id: Long): R<FlowDefinitionVo> {
        val vo = flowDefinitionService.queryById(id)
        return R.ok(vo)
    }

    /**
     * 新增流程定义
     */
    @SaCheckPermission("workflow:definition:add")
    @Log(title = "流程定义", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    fun add(@Validated(AddGroup::class) @RequestBody bo: FlowDefinitionBo): R<Void> {
        val result = flowDefinitionService.insertByBo(bo)
        return toAjax(result)
    }

    /**
     * 修改流程定义
     */
    @SaCheckPermission("workflow:definition:edit")
    @Log(title = "流程定义", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    fun edit(@Validated(EditGroup::class) @RequestBody bo: FlowDefinitionBo): R<Void> {
        val result = flowDefinitionService.updateByBo(bo)
        return toAjax(result)
    }

    /**
     * 取消发布流程定义
     *
     * @param id 流程定义id
     */
    @Log(title = "流程定义", businessType = BusinessType.INSERT)
    @PutMapping("/publish/{id}")
    @RepeatSubmit
    fun publish(@PathVariable id: Long): R<Boolean> {
        return R.ok(flowDefinitionService.publish(id))
    }

    /**
     * 取消发布流程定义
     */
    @Log(title = "流程定义", businessType = BusinessType.INSERT)
    @PutMapping("/unPublish/{id}")
    @RepeatSubmit
    @Transactional(rollbackFor = [java.lang.Exception::class])
    fun unPublish(@PathVariable id: Long): R<Boolean> {
        return R.ok(flowDefinitionService.unPublish(id))
    }

    /**
     * 删除流程定义
     */
    @SaCheckPermission("workflow:definition:remove")
    @Log(title = "流程定义", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    fun remove(@PathVariable ids: Array<Long>): R<Void> {
        val result = flowDefinitionService.removeDef(ids.toList())
        return toAjax(result)
    }

    /**
     * 导出流程定义
     */
    @Log(title = "流程定义", businessType = BusinessType.EXPORT)
    @PostMapping("/exportDef/{id}")
    fun exportDef(@PathVariable id: Long): R<String> {
        val flowJson = flowDefinitionService.exportDef(id)
        return if (flowJson != null) R.ok(flowJson, "导出成功") else R.fail("导出失败")
    }

    /**
     * 导入流程定义
     */
    @SaCheckPermission("workflow:definition:import")
    @Log(title = "流程定义", businessType = BusinessType.IMPORT)
    @RepeatSubmit()
    @PostMapping("/import")
    fun import(
        @RequestBody flowJson: String,
        @RequestParam(required = false) category: String?
    ): R<Void> {
        val result = flowDefinitionService.importJson(flowJson, category)
        return toAjax(result)
    }

    /**
     * 复制流程定义
     */
    @Log(title = "流程定义", businessType = BusinessType.INSERT)
    @PostMapping("/copy/{id}")
    @RepeatSubmit
    @Transactional(rollbackFor = [Exception::class])
    fun copy(@PathVariable id: Long): R<Void> {
        val result = flowDefinitionService.copy(id)
        return toAjax(result)
    }
}
