package com.github.alphafoxz.foxden.app.workflow.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.domain.dto.StartProcessReturnDTO
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.workflow.bo.*
import com.github.alphafoxz.foxden.domain.workflow.service.FlowNodeData
import com.github.alphafoxz.foxden.domain.workflow.service.FlowTaskService
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowTaskVo
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 任务Controller
 *
 * @author AprilWind
 */
@Validated
@RestController
@RequestMapping("/workflow/task")
class FlwTaskController(
    private val flowTaskService: FlowTaskService
) : BaseController() {

    /**
     * 启动流程
     */
    @SaCheckPermission("workflow:task:start")
    @Log(title = "任务管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/startWorkFlow")
    fun startWorkFlow(@Valid @RequestBody startProcessBo: StartProcessBo): R<StartProcessReturnDTO> {
        val result = flowTaskService.startWorkFlow(startProcessBo)
        return R.ok(result)
    }

    /**
     * 办理任务
     */
    @SaCheckPermission("workflow:task:complete")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/completeTask")
    fun completeTask(@Valid @RequestBody completeTaskBo: CompleteTaskBo): R<Void> {
        val result = flowTaskService.completeTask(completeTaskBo)
        return toAjax(result)
    }

    /**
     * 查询当前用户待办任务
     */
    @SaCheckPermission("workflow:task:waitList")
    @GetMapping("/pageByTaskWait")
    fun pageByTaskWait(
        flowTaskBo: FlowTaskBo, pageQuery: PageQuery
    ): TableDataInfo<FlowTaskVo> {
        return flowTaskService.pageByTaskWait(flowTaskBo, pageQuery)
    }

    /**
     * 查询当前租户所有待办任务
     */
    @SaCheckPermission("workflow:task:allWaitList")
    @GetMapping("/pageByAllTaskWait")
    fun pageByAllTaskWait(
        flowTaskBo: FlowTaskBo, pageQuery: PageQuery
    ): TableDataInfo<FlowTaskVo> {
        return flowTaskService.pageByAllTaskWait(flowTaskBo, pageQuery)
    }

    /**
     * 查询当前用户已办任务
     */
    @SaCheckPermission("workflow:task:finishList")
    @GetMapping("/pageByTaskFinish")
    fun pageByTaskFinish(
        flowTaskBo: FlowTaskBo, pageQuery: PageQuery
    ): TableDataInfo<com.github.alphafoxz.foxden.domain.workflow.vo.FlowHisTaskVo> {
        return flowTaskService.pageByTaskFinish(flowTaskBo, pageQuery)
    }

    /**
     * 查询当前租户所有已办任务
     */
    @SaCheckPermission("workflow:task:allFinishList")
    @GetMapping("/pageByAllTaskFinish")
    fun pageByAllTaskFinish(
        flowTaskBo: FlowTaskBo, pageQuery: PageQuery
    ): TableDataInfo<com.github.alphafoxz.foxden.domain.workflow.vo.FlowHisTaskVo> {
        return flowTaskService.pageByAllTaskFinish(flowTaskBo, pageQuery)
    }

    /**
     * 查询当前用户抄送
     */
    @SaCheckPermission("workflow:task:copyList")
    @GetMapping("/pageByTaskCopy")
    fun pageByTaskCopy(
        flowTaskBo: FlowTaskBo, pageQuery: PageQuery
    ): TableDataInfo<FlowTaskVo> {
        return flowTaskService.pageByTaskCopy(flowTaskBo, pageQuery)
    }

    /**
     * 获取任务详细信息
     */
    @SaCheckPermission("workflow:task:query")
    @GetMapping("/{taskId}")
    fun getTask(@PathVariable taskId: Long): R<FlowTaskVo> {
        val vo = flowTaskService.selectById(taskId)
        return R.ok(vo)
    }

    /**
     * 获取下一节点信息
     */
    @SaCheckPermission("workflow:task:query")
    @PostMapping("/getNextNodeList")
    fun getNextNodeList(@RequestBody bo: FlowNextNodeBo): R<List<FlowNodeData>> {
        val nodeList = flowTaskService.getNextNodeList(bo)
        return R.ok(nodeList)
    }

    /**
     * 驳回审批
     */
    @SaCheckPermission("workflow:task:back")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/backProcess")
    fun backProcess(@Valid @RequestBody bo: BackProcessBo): R<Void> {
        val result = flowTaskService.backProcess(bo)
        return toAjax(result)
    }

    /**
     * 终止任务
     */
    @SaCheckPermission("workflow:task:termination")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/terminationTask")
    fun terminationTask(@Valid @RequestBody bo: FlowTerminationBo): R<Void> {
        val result = flowTaskService.terminationTask(bo)
        return toAjax(result)
    }

    /**
     * 取消流程
     */
    @SaCheckPermission("workflow:task:cancel")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/cancelProcess")
    fun cancelProcess(@Valid @RequestBody bo: FlowCancelBo): R<Void> {
        val result = flowTaskService.cancelProcess(bo)
        return toAjax(result)
    }

    /**
     * 作废流程
     */
    @SaCheckPermission("workflow:task:invalid")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/invalidProcess")
    fun invalidProcess(@Valid @RequestBody bo: FlowInvalidBo): R<Void> {
        val result = flowTaskService.invalidProcess(bo)
        return toAjax(result)
    }

    /**
     * 委派任务
     */
    @SaCheckPermission("workflow:task:delegate")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/delegateTask")
    fun delegateTask(@Valid @RequestBody bo: TaskOperationBo): R<Void> {
        val result = flowTaskService.taskOperation(bo, "delegateTask")
        return toAjax(result)
    }

    /**
     * 转办任务
     */
    @SaCheckPermission("workflow:task:transfer")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/transferTask")
    fun transferTask(@Valid @RequestBody bo: TaskOperationBo): R<Void> {
        val result = flowTaskService.taskOperation(bo, "transferTask")
        return toAjax(result)
    }

    /**
     * 加签
     */
    @SaCheckPermission("workflow:task:addSignature")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/addSignature")
    fun addSignature(@Valid @RequestBody bo: TaskOperationBo): R<Void> {
        val result = flowTaskService.taskOperation(bo, "addSignature")
        return toAjax(result)
    }

    /**
     * 减签
     */
    @SaCheckPermission("workflow:task:reductionSignature")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/reductionSignature")
    fun reductionSignature(@Valid @RequestBody bo: TaskOperationBo): R<Void> {
        val result = flowTaskService.taskOperation(bo, "reductionSignature")
        return toAjax(result)
    }

    /**
     * 判断流程是否已结束
     */
    @GetMapping("/isTaskEnd/{instanceId}")
    fun isTaskEnd(@PathVariable instanceId: Long): R<Boolean> {
        val isEnd = flowTaskService.isTaskEnd(instanceId)
        return R.ok(isEnd)
    }
}
