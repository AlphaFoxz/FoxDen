package com.github.alphafoxz.foxden.app.workflow.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.validate.AddGroup
import com.github.alphafoxz.foxden.common.core.validate.EditGroup
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowSpelBo
import com.github.alphafoxz.foxden.domain.workflow.service.FlowSpelService
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowSpelVo
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 流程spel表达式定义Controller
 *
 * @author AprilWind
 */
@Validated
@RestController
@RequestMapping("/workflow/spel")
class FlowSpelController(
    private val flowSpelService: FlowSpelService
) : BaseController() {

    /**
     * 查询流程spel表达式定义列表
     */
    @SaCheckPermission("workflow:spel:list")
    @GetMapping("/list")
    fun list(bo: FlowSpelBo, pageQuery: PageQuery): TableDataInfo<FlowSpelVo> {
        return flowSpelService.queryPageList(bo, pageQuery)
    }

    /**
     * 获取流程spel表达式定义详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("workflow:spel:query")
    @GetMapping("/{id}")
    fun getInfo(@PathVariable id: Long?): R<FlowSpelVo?> {
        return R.ok(flowSpelService.queryById(id))
    }

    /**
     * 新增流程spel表达式定义
     */
    @SaCheckPermission("workflow:spel:add")
    @Log(title = "流程spel表达式定义", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    fun add(@Validated(AddGroup::class) @RequestBody bo: FlowSpelBo): R<Void> {
        return toAjax(flowSpelService.insertByBo(bo))
    }

    /**
     * 修改流程spel表达式定义
     */
    @SaCheckPermission("workflow:spel:edit")
    @Log(title = "流程spel表达式定义", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    fun edit(@Validated(EditGroup::class) @RequestBody bo: FlowSpelBo): R<Void> {
        return toAjax(flowSpelService.updateByBo(bo))
    }

    /**
     * 删除流程spel表达式定义
     *
     * @param ids 主键串
     */
    @SaCheckPermission("workflow:spel:remove")
    @Log(title = "流程spel表达式定义", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    fun remove(@PathVariable ids: LongArray): R<Void> {
        return toAjax(flowSpelService.deleteWithValidByIds(ids.toList(), true))
    }
}
