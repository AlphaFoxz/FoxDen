package com.github.alphafoxz.foxden.domain.workflow.service.impl

import com.github.alphafoxz.foxden.common.core.domain.dto.UserDTO
import com.github.alphafoxz.foxden.common.core.enums.BusinessStatusEnum
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.MapstructUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.domain.workflow.adapter.WorkflowEngineAdapter
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowCancelBo
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowInstanceBo
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowVariableBo
import com.github.alphafoxz.foxden.domain.workflow.entity.FoxFlowInstance
import com.github.alphafoxz.foxden.domain.workflow.entity.FoxFlowTask
import com.github.alphafoxz.foxden.domain.workflow.service.FlowInstanceService
import com.github.alphafoxz.foxden.domain.workflow.service.FlowTaskService
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowHisTaskVo
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowInstanceVo
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.dromara.warm.flow.core.FlowEngine
import org.dromara.warm.flow.core.constant.ExceptionCons
import org.dromara.warm.flow.core.dto.FlowParams
import org.dromara.warm.flow.core.entity.Definition
import org.dromara.warm.flow.core.entity.Instance
import org.dromara.warm.flow.core.entity.Task
import org.dromara.warm.flow.core.enums.NodeType
import org.dromara.warm.flow.core.service.DefService
import org.dromara.warm.flow.core.service.InsService
import org.dromara.warm.flow.core.service.TaskService
import org.dromara.warm.flow.orm.entity.FlowHisTask
import org.dromara.warm.flow.orm.entity.FlowUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 流程实例Service业务层处理
 *
 * @author AprilWind
 */
@Service
class FlowInstanceServiceImpl(
    private val sqlClient: KSqlClient,
    private val workflowEngineAdapter: WorkflowEngineAdapter,
    private val flowTaskService: FlowTaskService
) : FlowInstanceService {

    private val log = LoggerFactory.getLogger(FlowInstanceServiceImpl::class.java)

    // WarmFlow services
    private val insService: InsService = FlowEngine.insService()
    private val defService: DefService = FlowEngine.defService()
    private val taskService: TaskService = FlowEngine.taskService()
    private val hisTaskService = FlowEngine.hisTaskService()

    override fun selectRunningInstanceList(
        flowInstanceBo: FlowInstanceBo,
        pageQuery: PageQuery
    ): TableDataInfo<FlowInstanceVo> {
        // 查询流程实例
        val instances = sqlClient.createQuery(FoxFlowInstance::class) {
            select(table)
        }.execute()

        // 过滤运行中的流程实例
        val runningInstances = instances.filter { instance ->
            instance.flowStatus in BusinessStatusEnum.runningStatus()
        }

        // 过滤条件
        val filteredInstances = runningInstances.filter { instance ->
            var match = true

            // nodeName 模糊查询
            if (StringUtils.isNotBlank(flowInstanceBo.nodeName)) {
                match = match && (instance.nodeName?.contains(flowInstanceBo.nodeName!!) == true)
            }

            // businessId 精确查询
            if (StringUtils.isNotBlank(flowInstanceBo.businessId)) {
                match = match && (instance.businessId == flowInstanceBo.businessId)
            }

            // createByIds 查询
            if (flowInstanceBo.createByIds?.isNotEmpty() == true) {
                match = match && (instance.createBy in flowInstanceBo.createByIds!!)
            }

            match
        }

        // 转换为VO并填充流程定义信息
        val instanceVos = filteredInstances.map { convertToFlowInstanceVo(it) }

        val result = TableDataInfo<FlowInstanceVo>()
        result.code = 200
        result.msg = "查询成功"
        result.rows = instanceVos
        result.total = instanceVos.size.toLong()
        return result
    }

    override fun selectFinishInstanceList(
        flowInstanceBo: FlowInstanceBo,
        pageQuery: PageQuery
    ): TableDataInfo<FlowInstanceVo> {
        // 查询流程实例
        val instances = sqlClient.createQuery(FoxFlowInstance::class) {
            select(table)
        }.execute()

        // 过滤已结束的流程实例
        val finishedInstances = instances.filter { instance ->
            instance.flowStatus in BusinessStatusEnum.finishStatus()
        }

        // 过滤条件
        val filteredInstances = finishedInstances.filter { instance ->
            var match = true

            if (StringUtils.isNotBlank(flowInstanceBo.nodeName)) {
                match = match && (instance.nodeName?.contains(flowInstanceBo.nodeName!!) == true)
            }

            if (StringUtils.isNotBlank(flowInstanceBo.businessId)) {
                match = match && (instance.businessId == flowInstanceBo.businessId)
            }

            if (flowInstanceBo.createByIds?.isNotEmpty() == true) {
                match = match && (instance.createBy in flowInstanceBo.createByIds!!)
            }

            match
        }

        val instanceVos = filteredInstances.map { convertToFlowInstanceVo(it) }

        val result = TableDataInfo<FlowInstanceVo>()
        result.code = 200
        result.msg = "查询成功"
        result.rows = instanceVos
        result.total = instanceVos.size.toLong()
        return result
    }

    override fun queryByBusinessId(businessId: String): FlowInstanceVo? {
        val instance = selectInstByBusinessId(businessId) ?: return null
        return convertToFlowInstanceVo(instance)
    }

    override fun queryDetailById(instanceId: Long): FlowInstanceVo? {
        val instance = selectInstById(instanceId) ?: return null
        return convertToFlowInstanceVo(instance)
    }

    override fun selectCurrentInstanceList(
        flowInstanceBo: FlowInstanceBo,
        pageQuery: PageQuery
    ): TableDataInfo<FlowInstanceVo> {
        // 查询当前用户的流程实例
        val currentUserId = LoginHelper.getUserIdStr()

        val instances = sqlClient.createQuery(FoxFlowInstance::class) {
            select(table)
        }.execute()

        // 过滤当前用户的流程实例
        val userInstances = instances.filter { instance ->
            instance.createBy == currentUserId
        }

        // 过滤条件
        val filteredInstances = userInstances.filter { instance ->
            var match = true

            if (StringUtils.isNotBlank(flowInstanceBo.nodeName)) {
                match = match && (instance.nodeName?.contains(flowInstanceBo.nodeName!!) == true)
            }

            if (StringUtils.isNotBlank(flowInstanceBo.businessId)) {
                match = match && (instance.businessId == flowInstanceBo.businessId)
            }

            match
        }

        val instanceVos = filteredInstances.map { convertToFlowInstanceVo(it) }

        val result = TableDataInfo<FlowInstanceVo>()
        result.code = 200
        result.msg = "查询成功"
        result.rows = instanceVos
        result.total = instanceVos.size.toLong()
        return result
    }

    override fun flowHisTaskList(businessId: String): Map<String, Any> {
        val flowInstance = selectInstByBusinessId(businessId)
            ?: throw ServiceException(ExceptionCons.NOT_FOUNT_INSTANCE)

        val instanceId = flowInstance.id
        val combinedList = mutableListOf<FlowHisTaskVo>()

        // 查询运行中的任务
        val runningTasks = sqlClient.createQuery(FoxFlowTask::class) {
            select(table)
        }.execute().filter { it.instanceId == instanceId }

        if (runningTasks.isNotEmpty()) {
            val runningTaskVos = runningTasks.mapNotNull { MapstructUtils.convert(it, FlowHisTaskVo::class.java)?.apply {
                this.flowStatus = "0" // TaskStatusEnum.WAITING.getStatus()
                this.updateTime = null
                this.approver = null // 将从用户表填充
            } }

            // 获取任务关联的用户
            val taskIds = runningTasks.map { it.id }
            val associatedUsers = FlowEngine.userService().getByAssociateds(taskIds)
            val taskUserMap = associatedUsers.groupBy { it.associated }

            // 填充处理人
            runningTaskVos.forEach { vo ->
                val users = taskUserMap[vo.id]
                if (users?.isNotEmpty() == true) {
                    vo.approver = users.joinToString(",") { it.processedBy ?: "" }
                }
            }

            combinedList.addAll(runningTaskVos)
        }

        // 查询历史任务
        val hisTasks = hisTaskService.getByInsId(instanceId)
        if (hisTasks.isNotEmpty()) {
            val hisTaskVos = hisTasks
                .filter { it.nodeType == NodeType.BETWEEN.key }
                .sortedByDescending { it.updateTime }
                .mapNotNull { MapstructUtils.convert(it, FlowHisTaskVo::class.java) }
            combinedList.addAll(hisTaskVos)
        }

        return mapOf("list" to combinedList, "instanceId" to instanceId)
    }

    override fun instanceVariable(instanceId: Long): Map<String, Any> {
        val flowInstance = selectInstById(instanceId)
            ?: throw ServiceException(ExceptionCons.NOT_FOUNT_INSTANCE)

        val variableMap = flowInstance.variable?.let { parseVariables(it) } ?: emptyMap()
        val variableList = variableMap.map { (key, value) ->
            mapOf("key" to key, "value" to (value ?: ""))
        }

        return mapOf(
            "variableList" to variableList,
            "variable" to (flowInstance.variable ?: "{}")
        )
    }

    @Transactional
    override fun updateVariable(bo: FlowVariableBo): Boolean {
        val flowInstance = selectInstById(bo.instanceId!!)
            ?: throw ServiceException(ExceptionCons.NOT_FOUNT_INSTANCE)

        val variables = parseVariables(flowInstance.variable ?: "{}").toMutableMap()
        val boVariables = bo.variables ?: emptyMap()

        // 检查并更新变量
        var hasChange = false
        for ((key, value) in boVariables) {
            if (key in variables) {
                variables[key] = value
                hasChange = true
            } else {
                log.warn("变量不存在: {}", key)
            }
        }

        if (hasChange) {
            // 需要更新实例的变量
            log.info("更新流程变量 - instanceId: {}, variables: {}", bo.instanceId, variables.keys)
            // 注意：这里需要 WarmFlow 提供更新方法，暂时使用 mergeVariable
            val instance = insService.getById(bo.instanceId)
            if (instance != null) {
                taskService.mergeVariable(instance, variables)
                insService.updateById(instance)
                return true
            }
        }

        return false
    }

    @Transactional
    override fun deleteByIds(instanceIds: List<Long>): Boolean {
        val instances = insService.getByIds(instanceIds)
        if (instances.isEmpty()) {
            log.warn("未找到对应的流程实例信息，无法执行删除操作。")
            return false
        }

        // 获取定义信息
        val definitionIds = instances.map { it.definitionId }.distinct()
        val definitions = defService.getByIds(definitionIds)
        val definitionMap = definitions.associateBy { it.id }

        // 逐一触发删除事件
        instances.forEach { instance ->
            val definition = definitionMap[instance.definitionId]
            if (definition != null) {
                // TODO: 调用 flowProcessEventHandler.processDeleteHandler(definition.flowCode, instance.businessId)
                log.info("触发删除事件 - flowCode: {}, businessId: {}", definition.flowCode, instance.businessId)
            } else {
                log.warn("实例 ID: {} 对应的流程定义信息未找到，跳过删除事件触发。", instance.id)
            }
        }

        // 删除实例
        return insService.remove(instanceIds)
    }

    @Transactional
    override fun deleteHisByInstanceIds(instanceIds: List<Long>): Boolean {
        val instances = insService.getByIds(instanceIds)
        if (instances.isEmpty()) {
            log.warn("未找到对应的流程实例信息，无法执行删除操作。")
            return false
        }

        // 获取定义信息
        val definitionIds = instances.map { it.definitionId }.distinct()
        val definitions = defService.getByIds(definitionIds)
        val definitionMap = definitions.associateBy { it.id }

        // 逐一触发删除事件
        instances.forEach { instance ->
            val definition = definitionMap[instance.definitionId]
            if (definition != null) {
                // TODO: 调用 flowProcessEventHandler.processDeleteHandler(definition.flowCode, instance.businessId)
                log.info("触发删除事件 - flowCode: {}, businessId: {}", definition.flowCode, instance.businessId)
            } else {
                log.warn("实例 ID: {} 对应的流程定义信息未找到，跳过删除事件触发。", instance.id)
            }
        }

        // 删除运行中的任务
        val tasks = sqlClient.createQuery(FoxFlowTask::class) {
            select(table)
        }.execute().filter { it.instanceId in instanceIds }

        if (tasks.isNotEmpty()) {
            val taskIds = tasks.map { it.id }
            FlowEngine.userService().deleteByTaskIds(taskIds)
        }

        FlowEngine.taskService().deleteByInsIds(instanceIds)
        FlowEngine.hisTaskService().deleteByInsIds(instanceIds)
        insService.removeByIds(instanceIds)

        return true
    }

    override fun selectInstByBusinessId(businessId: String): FoxFlowInstance? {
        val instances = sqlClient.createQuery(FoxFlowInstance::class) {
            select(table)
        }.execute()
        return instances.firstOrNull { it.businessId == businessId }
    }

    override fun selectInstById(instanceId: Long): FoxFlowInstance? {
        return sqlClient.findById(FoxFlowInstance::class, instanceId)
    }

    override fun selectByTaskId(taskId: Long): FoxFlowInstance? {
        val task = taskService.getById(taskId)
        if (task != null) {
            return selectInstById(task.instanceId)
        }

        // 查询历史任务
        val hisTask = hisTaskService.getById(taskId)
        if (hisTask != null) {
            return selectInstById(hisTask.instanceId)
        }

        return null
    }

    @Transactional
    override fun updateStatus(instanceId: Long, status: String) {
        val instance = insService.getById(instanceId) ?: return
        instance.flowStatus = status
        insService.updateById(instance)
    }

    @Transactional
    override fun deleteByBusinessIds(businessIds: List<String>): Boolean {
        val instances = sqlClient.createQuery(FoxFlowInstance::class) {
            select(table)
        }.execute().filter { it.businessId in businessIds }

        if (instances.isEmpty()) {
            log.warn("未找到对应的流程实例信息，无法执行删除操作。")
            return false
        }

        val instanceIds = instances.map { it.id }
        return insService.remove(instanceIds)
    }

    @Transactional
    override fun cancelProcessApply(bo: FlowCancelBo): Boolean {
        val instance = selectInstByBusinessId(bo.businessId!!)
            ?: throw ServiceException(ExceptionCons.NOT_FOUNT_INSTANCE)

        val definition = defService.getById(instance.definitionId)
            ?: throw ServiceException(ExceptionCons.NOT_FOUNT_DEF)

        BusinessStatusEnum.checkCancelStatus(instance.flowStatus)

        val flowParams = FlowParams.build()
            .message(bo.message)
            .flowStatus(BusinessStatusEnum.CANCEL.status)
            .hisStatus(BusinessStatusEnum.CANCEL.status)
            .handler(LoginHelper.getUserIdStr())
            .ignore(true)

        taskService.revoke(instance.id, flowParams)
        return true
    }

    @Transactional
    override fun active(instanceId: Long): Boolean {
        val instance = insService.getById(instanceId) ?: return false
        instance.flowStatus = BusinessStatusEnum.WAITING.status
        insService.updateById(instance)
        return true
    }

    @Transactional
    override fun unActive(instanceId: Long): Boolean {
        val instance = insService.getById(instanceId) ?: return false
        instance.flowStatus = BusinessStatusEnum.DRAFT.status
        insService.updateById(instance)
        return true
    }

    /**
     * 将 FoxFlowInstance 转换为 FlowInstanceVo 并填充流程定义信息
     */
    private fun convertToFlowInstanceVo(instance: FoxFlowInstance): FlowInstanceVo {
        val vo = MapstructUtils.convert(instance, FlowInstanceVo::class.java) ?: FlowInstanceVo()

        // 查询流程定义并填充信息
        val definition = defService.getById(instance.definitionId)
        if (definition != null) {
            vo.flowName = definition.flowName
            vo.flowCode = definition.flowCode
            vo.version = definition.version
            vo.formCustom = definition.formCustom
            vo.formPath = definition.formPath
            vo.category = definition.category
        }

        return vo
    }

    /**
     * 解析 JSON 格式的变量字符串为 Map
     */
    private fun parseVariables(variables: String): Map<String, Any?> {
        if (variables.isBlank()) return emptyMap()
        return try {
            com.fasterxml.jackson.databind.ObjectMapper().readValue(
                variables,
                object : com.fasterxml.jackson.core.type.TypeReference<Map<String, Any?>>() {})
        } catch (e: Exception) {
            log.warn("解析流程变量失败: {}", e.message)
            emptyMap()
        }
    }
}
