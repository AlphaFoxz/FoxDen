package com.github.alphafoxz.foxden.domain.workflow.adapter

import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.domain.workflow.entity.FoxFlowDefinition
import com.github.alphafoxz.foxden.domain.workflow.entity.FoxFlowInstance
import com.github.alphafoxz.foxden.domain.workflow.entity.FoxFlowNode
import com.github.alphafoxz.foxden.domain.workflow.entity.FoxFlowTask
import com.github.alphafoxz.foxden.domain.workflow.entity.FoxFlowUser
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * 工作流引擎适配器
 * 用于在Jimmer ORM和工作流引擎之间进行数据转换
 *
 * @author AprilWind
 */
@Component
class WorkflowEngineAdapter(
    private val sqlClient: KSqlClient
) {
    private val log = LoggerFactory.getLogger(WorkflowEngineAdapter::class.java)

    /**
     * 检查工作流引擎是否可用
     */
    fun isEngineAvailable(): Boolean {
        return try {
            sqlClient.createQuery(FoxFlowDefinition::class) {
                select(table)
            }.limit(1).execute().isNotEmpty()
        } catch (e: Exception) {
            log.warn("工作流引擎检查失败: {}", e.message)
            false
        }
    }

    /**
     * 查询流程定义
     */
    fun findFlowDefinition(flowCode: String): FoxFlowDefinition? {
        val definitions = sqlClient.createQuery(FoxFlowDefinition::class) {
            select(table)
        }.execute()
        return definitions.find { it.flowCode == flowCode }
    }

    /**
     * 根据ID查询流程定义
     */
    fun findFlowDefinitionById(id: Long): FoxFlowDefinition? {
        return sqlClient.findById(FoxFlowDefinition::class, id)
    }

    /**
     * 查询流程实例
     */
    fun findFlowInstance(instanceId: Long): FoxFlowInstance? {
        return sqlClient.findById(FoxFlowInstance::class, instanceId)
    }

    /**
     * 根据业务ID查询流程实例
     */
    fun findFlowInstanceByBusinessId(businessId: String): FoxFlowInstance? {
        val instances = sqlClient.createQuery(FoxFlowInstance::class) {
            select(table)
        }.execute()
        return instances.find { it.businessId == businessId }
    }

    /**
     * 查询任务
     */
    fun findFlowTask(taskId: Long): FoxFlowTask? {
        return sqlClient.findById(FoxFlowTask::class, taskId)
    }

    /**
     * 查询任务关联的用户
     */
    fun findUsersByTaskId(taskId: Long): List<FoxFlowUser> {
        val users = sqlClient.createQuery(FoxFlowUser::class) {
            select(table)
        }.execute()
        return users.filter { it.associated == taskId }
    }

    /**
     * 查询流程定义的所有节点
     */
    fun findNodesByDefinitionId(definitionId: Long): List<FoxFlowNode> {
        val nodes = sqlClient.createQuery(FoxFlowNode::class) {
            select(table)
        }.execute()
        return nodes.filter { it.definitionId == definitionId }
            .sortedBy { it.nodeRatio?.toIntOrNull() ?: 0 }
    }

    /**
     * 查询流程定义的某个节点
     */
    fun findNodeByCode(definitionId: Long, nodeCode: String): FoxFlowNode? {
        val nodes = sqlClient.createQuery(FoxFlowNode::class) {
            select(table)
        }.execute()
        return nodes.find { it.definitionId == definitionId && it.nodeCode == nodeCode }
    }

    /**
     * 验证流程定义是否已发布
     */
    fun isFlowPublished(flowCode: String): Boolean {
        val definition = findFlowDefinition(flowCode)
        return definition?.published == 1
    }

    /**
     * 验证流程定义是否存在
     */
    fun isFlowExists(flowCode: String): Boolean {
        return findFlowDefinition(flowCode) != null
    }

    /**
     * 获取流程的下一个节点
     */
    fun getNextNode(definitionId: Long, currentNodeCode: String): FoxFlowNode? {
        val nodes = findNodesByDefinitionId(definitionId)
        val currentIndex = nodes.indexOfFirst { it.nodeCode == currentNodeCode }
        return if (currentIndex >= 0 && currentIndex < nodes.size - 1) {
            nodes[currentIndex + 1]
        } else {
            null
        }
    }

    /**
     * 验证流程状态是否允许操作
     */
    fun validateFlowStatus(flowStatus: String, allowedStatuses: List<String>) {
        if (flowStatus !in allowedStatuses) {
            throw ServiceException("当前流程状态[$flowStatus]不允许此操作，允许的状态: $allowedStatuses")
        }
    }

    /**
     * 获取流程定义的版本号
     */
    fun getFlowVersion(flowCode: String): String {
        val definition = findFlowDefinition(flowCode)
        return definition?.version ?: "0"
    }

    /**
     * 检查流程实例是否已结束
     */
    fun isInstanceFinished(instanceId: Long): Boolean {
        val instance = findFlowInstance(instanceId)
        return instance?.flowStatus == "1"
    }

    /**
     * 获取流程实例的业务ID
     */
    fun getBusinessId(instanceId: Long): String? {
        val instance = findFlowInstance(instanceId)
        return instance?.businessId
    }
}
