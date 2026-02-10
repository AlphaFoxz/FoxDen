package com.github.alphafoxz.foxden.domain.workflow.service.impl

import com.github.alphafoxz.foxden.common.core.domain.dto.StartProcessReturnDTO
import com.github.alphafoxz.foxden.common.core.enums.BusinessStatusEnum
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.MapstructUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.domain.workflow.adapter.WorkflowEngineAdapter
import com.github.alphafoxz.foxden.domain.workflow.bo.*
import com.github.alphafoxz.foxden.domain.workflow.entity.FoxFlowTask
import com.github.alphafoxz.foxden.domain.workflow.service.FlowCommonService
import com.github.alphafoxz.foxden.domain.workflow.service.FlowNodeData
import com.github.alphafoxz.foxden.domain.workflow.service.FlowTaskService
import com.github.alphafoxz.foxden.domain.workflow.service.FlowTaskAssigneeService
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowHisTaskVo
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowTaskVo
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.dromara.warm.flow.core.FlowEngine
import org.dromara.warm.flow.core.dto.FlowParams
import org.dromara.warm.flow.core.entity.Definition
import org.dromara.warm.flow.core.entity.Instance
import org.dromara.warm.flow.core.entity.Task
import org.dromara.warm.flow.core.enums.NodeType
import org.dromara.warm.flow.core.enums.SkipType
import org.dromara.warm.flow.core.service.DefService
import org.dromara.warm.flow.core.service.InsService
import org.dromara.warm.flow.core.service.NodeService
import org.dromara.warm.flow.core.service.TaskService
import org.dromara.warm.flow.orm.entity.FlowHisTask
import org.dromara.warm.flow.orm.entity.FlowUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 任务Service业务层处理
 *
 * @author AprilWind
 */
@Service
class FlowTaskServiceImpl(
    private val sqlClient: KSqlClient,
    private val workflowEngineAdapter: WorkflowEngineAdapter,
    private val flowCommonService: FlowCommonService,
    private val flowTaskAssigneeService: FlowTaskAssigneeService
) : FlowTaskService {

    private val log = LoggerFactory.getLogger(FlowTaskServiceImpl::class.java)

    // WarmFlow services
    private val taskService: TaskService = FlowEngine.taskService()
    private val insService: InsService = FlowEngine.insService()
    private val defService: DefService = FlowEngine.defService()
    private val hisTaskService = FlowEngine.hisTaskService()
    private val nodeService: NodeService = FlowEngine.nodeService()

    override fun startWorkFlow(startProcessBo: StartProcessBo): StartProcessReturnDTO? {
        val businessId = startProcessBo.businessId!!
        if (StringUtils.isBlank(businessId)) {
            throw ServiceException("启动工作流时必须包含业务ID")
        }

        // 设置流程变量
        val variables = startProcessBo.getVariables().toMutableMap()
        variables["initiator"] = LoginHelper.getUserIdStr() ?: ""
        variables["initiatorDeptId"] = LoginHelper.getDeptId() ?: 0L
        variables["businessId"] = businessId

        // 检查是否已存在流程实例
        val existingInstance = workflowEngineAdapter.findFlowInstanceByBusinessId(businessId)
        if (existingInstance != null) {
            // 已存在流程，验证状态
            BusinessStatusEnum.checkStartStatus(existingInstance.flowStatus ?: "")

            val tasks = sqlClient.createQuery(FoxFlowTask::class) {
                select(table)
            }.execute().filter { it.instanceId == existingInstance.id }

            if (tasks.isNotEmpty()) {
                // 合并变量 - 通过更新流程参数实现
                return StartProcessReturnDTO(
                    processInstanceId = existingInstance.id,
                    taskId = tasks[0].id
                )
            }
        }

        // 验证流程定义已发布
        val definition = workflowEngineAdapter.findFlowDefinition(startProcessBo.flowCode!!)
            ?: throw ServiceException("流程【${startProcessBo.flowCode}】未发布，请先在流程设计器中发布流程定义")

        if (definition.published != 1) {
            throw ServiceException("流程【${startProcessBo.flowCode}】未发布，请先在流程设计器中发布流程定义")
        }

        // 构建流程参数
        val flowParams = FlowParams.build()
            .flowCode(startProcessBo.flowCode)
            .handler(startProcessBo.handler)
            .variable(variables)
            .flowStatus(BusinessStatusEnum.DRAFT.status)

        // 启动流程
        val instance = insService.start(businessId, flowParams)

        // 查询第一个任务
        val tasks = sqlClient.createQuery(FoxFlowTask::class) {
            select(table)
        }.execute().filter { it.instanceId == instance.id }
        if (tasks.size != 1) {
            throw ServiceException("请检查流程第一个环节是否为申请人！")
        }

        return StartProcessReturnDTO(
            processInstanceId = instance.id,
            taskId = tasks[0].id
        )
    }

    @Transactional
    override fun completeTask(completeTaskBo: CompleteTaskBo): Boolean {
        val taskId = completeTaskBo.taskId ?: throw ServiceException("任务id不能为空")
        val variables = completeTaskBo.getVariables().toMutableMap()
        variables["flowCopyList"] = completeTaskBo.flowCopyList ?: emptyList<Any>()
        variables["messageType"] = completeTaskBo.messageType ?: emptyList<Any>()
        variables["messageNotice"] = completeTaskBo.notice ?: ""

        // 查询任务
        val task = taskService.getById(taskId)
            ?: throw ServiceException("流程任务不存在或任务已审批！")

        val instance = insService.getById(task.instanceId)

        // 检查流程状态
        if (BusinessStatusEnum.isDraftOrCancelOrBack(instance.flowStatus)) {
            variables["submit"] = true
        }

        // 构建流程参数
        val flowParams = FlowParams.build()
            .handler(completeTaskBo.handler)
            .variable(variables)
            .skipType(SkipType.PASS.key)
            .message(completeTaskBo.message)
            .flowStatus(BusinessStatusEnum.WAITING.status)

        // 完成任务
        taskService.skip(taskId, flowParams)

        return true
    }

    override fun pageByTaskWait(
        flowTaskBo: FlowTaskBo,
        pageQuery: PageQuery
    ): TableDataInfo<FlowTaskVo> {
        // 查询当前用户的待办任务
        val currentUserId = LoginHelper.getUserIdStr()!!

        val tasks = sqlClient.createQuery(FoxFlowTask::class) {
            select(table)
        }.execute().filter { it.flowStatus == BusinessStatusEnum.WAITING.status }

        // 查询用户关联的任务
        val userAssociatedTaskIds = flowTaskAssigneeService.getTaskIdsByUser(currentUserId)
        val filteredTasks = tasks.filter { it.id in userAssociatedTaskIds }

        // 转换为VO
        val taskVos = filteredTasks.map { convertToFlowTaskVo(it) }

        val result = TableDataInfo<FlowTaskVo>()
        result.code = 200
        result.msg = "查询成功"
        result.rows = taskVos
        result.total = taskVos.size.toLong()
        return result
    }

    override fun pageByAllTaskWait(
        flowTaskBo: FlowTaskBo,
        pageQuery: PageQuery
    ): TableDataInfo<FlowTaskVo> {
        // 查询所有待办任务
        val tasks = sqlClient.createQuery(FoxFlowTask::class) {
            select(table)
        }.execute().filter { it.flowStatus == BusinessStatusEnum.WAITING.status }

        // 转换为VO
        val taskVos = tasks.map { convertToFlowTaskVo(it) }

        val result = TableDataInfo<FlowTaskVo>()
        result.code = 200
        result.msg = "查询成功"
        result.rows = taskVos
        result.total = taskVos.size.toLong()
        return result
    }

    override fun pageByTaskFinish(
        flowTaskBo: FlowTaskBo,
        pageQuery: PageQuery
    ): TableDataInfo<FlowHisTaskVo> {
        // 查询当前用户的已办任务
        val currentUserId = LoginHelper.getUserIdStr()

        val hisTasks = hisTaskService.getByInsId(null)
        val userTasks = hisTasks.filter { it.approver == currentUserId }

        // 转换为VO
        val taskVos = userTasks.mapNotNull { MapstructUtils.convert(it, FlowHisTaskVo::class.java) }

        val result = TableDataInfo<FlowHisTaskVo>()
        result.code = 200
        result.msg = "查询成功"
        result.rows = taskVos
        result.total = taskVos.size.toLong()
        return result
    }

    override fun pageByAllTaskFinish(
        flowTaskBo: FlowTaskBo,
        pageQuery: PageQuery
    ): TableDataInfo<FlowHisTaskVo> {
        // 查询所有已办任务
        val hisTasks = hisTaskService.getByInsId(null)

        // 转换为VO
        val taskVos = hisTasks.mapNotNull { MapstructUtils.convert(it, FlowHisTaskVo::class.java) }

        val result = TableDataInfo<FlowHisTaskVo>()
        result.code = 200
        result.msg = "查询成功"
        result.rows = taskVos
        result.total = taskVos.size.toLong()
        return result
    }

    override fun pageByTaskCopy(
        flowTaskBo: FlowTaskBo,
        pageQuery: PageQuery
    ): TableDataInfo<FlowTaskVo> {
        // 查询当前用户的抄送任务
        val currentUserId = LoginHelper.getUserIdStr()!!

        val tasks = sqlClient.createQuery(FoxFlowTask::class) {
            select(table)
        }.execute()

        // 查询用户关联的抄送任务
        val copyTaskIds = flowTaskAssigneeService.getCopyTaskIdsByUser(currentUserId)
        val filteredTasks = tasks.filter { it.id in copyTaskIds && it.delFlag == "0" }

        // 转换为VO
        val taskVos = filteredTasks.map { convertToFlowTaskVo(it) }

        val result = TableDataInfo<FlowTaskVo>()
        result.code = 200
        result.msg = "查询成功"
        result.rows = taskVos
        result.total = taskVos.size.toLong()
        return result
    }

    @Transactional
    override fun backProcess(bo: BackProcessBo): Boolean {
        val taskId = bo.taskId
        val task = taskService.getById(taskId)
            ?: throw ServiceException("任务不存在！")

        val instance = insService.getById(task.instanceId)
        BusinessStatusEnum.checkBackStatus(instance.flowStatus)

        // 获取申请节点编码
        val applyNodeCode = flowCommonService.applyNodeCode(task.definitionId)

        // 构建流程参数
        val variables = mutableMapOf(
            "messageType" to (bo.messageType ?: emptyList<Any>()),
            "messageNotice" to (bo.notice ?: "")
        )

        val flowParams = FlowParams.build()
            .nodeCode(bo.nodeCode)
            .variable(variables)
            .message(bo.message)
            .skipType(SkipType.REJECT.key)
            .flowStatus(if (applyNodeCode == bo.nodeCode) "1" else BusinessStatusEnum.WAITING.status)
            .hisStatus("1")

        taskService.skip(task.id, flowParams)
        return true
    }

    @Transactional
    override fun terminationTask(bo: FlowTerminationBo): Boolean {
        val taskId = bo.taskId
        val task = taskService.getById(taskId)
            ?: throw ServiceException("任务不存在！")

        val instance = insService.getById(task.instanceId)
        if (instance != null) {
            BusinessStatusEnum.checkInvalidStatus(instance.flowStatus)
        }

        val flowParams = FlowParams.build()
            .message(bo.comment)
            .flowStatus(BusinessStatusEnum.TERMINATION.status)
            .hisStatus("2")

        taskService.termination(taskId, flowParams)
        return true
    }

    @Transactional
    override fun cancelProcess(bo: FlowCancelBo): Boolean {
        val instanceId = bo.instanceId ?: return false
        val instance = insService.getById(instanceId)
            ?: throw ServiceException("流程实例不存在！")

        BusinessStatusEnum.checkCancelStatus(instance.flowStatus)

        // 使用 revoke 撤销流程
        instance.flowStatus = BusinessStatusEnum.CANCEL.status
        insService.updateById(instance)
        return true
    }

    @Transactional
    override fun invalidProcess(bo: FlowInvalidBo): Boolean {
        val instanceId = bo.instanceId ?: return false
        val instance = insService.getById(instanceId)
            ?: throw ServiceException("流程实例不存在！")

        BusinessStatusEnum.checkInvalidStatus(instance.flowStatus)

        // 作废流程 - 更新状态
        instance.flowStatus = BusinessStatusEnum.INVALID.status
        insService.updateById(instance)
        return true
    }

    @Transactional
    override fun taskOperation(bo: TaskOperationBo, taskOperation: String): Boolean {
        val flowParams = FlowParams.build().message(bo.message)

        // 根据操作类型设置参数
        when (taskOperation) {
            "delegateTask", "transferTask" -> {
                flowParams.addHandlers(listOf(bo.userId))
            }
            "addSignature" -> {
                flowParams.addHandlers(bo.userIds ?: emptyList())
            }
            "reductionSignature" -> {
                flowParams.reductionHandlers(bo.userIds ?: emptyList())
            }
            else -> {
                throw ServiceException("不支持的操作类型: $taskOperation")
            }
        }

        val taskId = bo.taskId
        val task = taskService.getById(taskId)

        // 执行任务操作
        when (taskOperation) {
            "delegateTask" -> {
                flowParams.hisStatus("3")
                return taskService.depute(taskId, flowParams)
            }
            "transferTask" -> {
                flowParams.hisStatus("4")
                return taskService.transfer(taskId, flowParams)
            }
            "addSignature" -> {
                flowParams.hisStatus("5")
                return taskService.addSignature(taskId, flowParams)
            }
            "reductionSignature" -> {
                flowParams.hisStatus("6")
                return taskService.reductionSignature(taskId, flowParams)
            }
            else -> {
                throw ServiceException("不支持的操作类型: $taskOperation")
            }
        }
    }

    override fun getNextNodeList(bo: FlowNextNodeBo): List<FlowNodeData> {
        val taskId = bo.taskId
        val task = taskService.getById(taskId)
            ?: return emptyList()

        val instance = insService.getById(task.instanceId)
        val variables = bo.variables ?: emptyMap()

        // 合并流程变量
        val mergedVariables = mutableMapOf<String, Any>()
        mergedVariables.putAll(instance.variableMap ?: emptyMap())
        mergedVariables.putAll(variables)

        // 获取下一节点列表
        val nextNodeList = nodeService.getNextNodeList(
            task.definitionId,
            task.nodeCode,
            null,
            SkipType.PASS.key,
            mergedVariables
        )

        // 过滤只保留中间节点
        return nextNodeList
            .filter { it.nodeType == NodeType.BETWEEN.key }
            .map { node ->
                FlowNodeData(
                    nodeCode = node.nodeCode,
                    nodeName = node.nodeName,
                    nodeType = node.nodeType.toString(),
                    nodeRatio = node.nodeRatio?.toString(),
                    coordinate = node.coordinate,
                    permissionList = null // permissionList 不在 Node 对象中
                )
            }
    }

    override fun isTaskEnd(instanceId: Long): Boolean {
        val tasks = sqlClient.createQuery(FoxFlowTask::class) {
            select(table)
        }.execute().filter { it.instanceId == instanceId && it.delFlag == "0" }

        return tasks.isEmpty()
    }

    override fun selectById(taskId: Long): FlowTaskVo? {
        val task = taskService.getById(taskId) ?: return null
        val instance = insService.getById(task.instanceId) ?: return null
        val definition = defService.getById(task.definitionId) ?: return null

        // 转换为VO
        val vo = MapstructUtils.convert(task, FlowTaskVo::class.java) ?: return null
        vo.flowStatus = instance.flowStatus
        vo.version = definition.version
        vo.flowCode = definition.flowCode
        vo.flowName = definition.flowName
        vo.businessId = instance.businessId

        // 查询节点信息
        val nodes = nodeService.getByNodeCodes(listOf(task.nodeCode), task.definitionId)
        if (nodes.isNotEmpty()) {
            val node = nodes[0]
            // nodeRatio 和 coordinate 等属性根据实际节点对象设置
        }

        return vo ?: FlowTaskVo()
    }

    private fun convertToFlowTaskVo(task: FoxFlowTask): FlowTaskVo {
        val instance = insService.getById(task.instanceId)
        val definition = defService.getById(task.definitionId)

        return MapstructUtils.convert(task, FlowTaskVo::class.java)?.apply {
            this.flowStatus = instance?.flowStatus
            this.version = definition?.version
            this.flowCode = definition?.flowCode
            this.flowName = definition?.flowName
            this.businessId = instance?.businessId
        } ?: FlowTaskVo()
    }
}
