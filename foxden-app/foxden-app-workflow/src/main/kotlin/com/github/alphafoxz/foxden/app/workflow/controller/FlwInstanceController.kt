package com.github.alphafoxz.foxden.app.workflow.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowCancelBo
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowInstanceBo
import com.github.alphafoxz.foxden.domain.workflow.service.FlowInstanceService
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowInstanceVo
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 流程实例Controller
 *
 * @author AprilWind
 */
@Validated
@RestController
@RequestMapping("/workflow/instance")
class FlwInstanceController(
    private val flowInstanceService: FlowInstanceService
) : BaseController() {

    /**
     * 查询正在运行的流程实例分页列表
     */
    @SaCheckPermission("workflow:instance:runningList")
    @GetMapping("/selectRunningInstanceList")
    fun selectRunningInstanceList(
        flowInstanceBo: FlowInstanceBo,
        pageQuery: PageQuery
    ): TableDataInfo<FlowInstanceVo> {
        return flowInstanceService.selectRunningInstanceList(flowInstanceBo, pageQuery)
    }

    /**
     * 查询已结束的流程实例分页列表
     */
    @SaCheckPermission("workflow:instance:finishList")
    @GetMapping("/selectFinishInstanceList")
    fun selectFinishInstanceList(
        flowInstanceBo: FlowInstanceBo,
        pageQuery: PageQuery
    ): TableDataInfo<FlowInstanceVo> {
        return flowInstanceService.selectFinishInstanceList(flowInstanceBo, pageQuery)
    }

    /**
     * 查询当前用户发起的流程实例
     */
    @SaCheckPermission("workflow:instance:currentList")
    @GetMapping("/selectCurrentInstanceList")
    fun selectCurrentInstanceList(
        flowInstanceBo: FlowInstanceBo,
        pageQuery: PageQuery
    ): TableDataInfo<FlowInstanceVo> {
        return flowInstanceService.selectCurrentInstanceList(flowInstanceBo, pageQuery)
    }

    /**
     * 获取流程实例详细信息
     */
    @SaCheckPermission("workflow:instance:query")
    @GetMapping("/{instanceId}")
    fun getInfo(@PathVariable instanceId: Long): R<FlowInstanceVo> {
        val vo = flowInstanceService.queryDetailById(instanceId)
        return R.ok(vo)
    }

    /**
     * 根据业务ID删除流程实例
     */
    @SaCheckPermission("workflow:instance:remove")
    @Log(title = "流程实例", businessType = BusinessType.DELETE)
    @RepeatSubmit()
    @DeleteMapping("/deleteByBusinessIds")
    fun deleteByBusinessIds(@RequestBody businessIds: List<String>): R<Void> {
        val result = flowInstanceService.deleteByBusinessIds(businessIds)
        return toAjax(result)
    }

    /**
     * 根据实例ID删除流程实例
     */
    @SaCheckPermission("workflow:instance:remove")
    @Log(title = "流程实例", businessType = BusinessType.DELETE)
    @RepeatSubmit()
    @DeleteMapping("/deleteByInstanceIds")
    fun deleteByInstanceIds(@RequestBody instanceIds: List<Long>): R<Void> {
        val result = flowInstanceService.deleteByIds(instanceIds)
        return toAjax(result)
    }

    /**
     * 取消流程申请
     */
    @SaCheckPermission("workflow:instance:cancel")
    @Log(title = "流程实例", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/cancelProcessApply")
    fun cancelProcessApply(@Valid @RequestBody bo: FlowCancelBo): R<Void> {
        val result = flowInstanceService.cancelProcessApply(bo)
        return toAjax(result)
    }

    /**
     * 激活流程实例
     */
    @SaCheckPermission("workflow:instance:active")
    @Log(title = "流程实例", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/active/{instanceId}")
    fun active(@PathVariable instanceId: Long): R<Void> {
        val result = flowInstanceService.active(instanceId)
        return toAjax(result)
    }
}
