package com.github.alphafoxz.foxden.domain.workflow.service.impl

import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.MapstructUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.workflow.adapter.WorkflowEngineAdapter
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowDefinitionBo
import com.github.alphafoxz.foxden.domain.workflow.entity.FoxFlowDefinition
import com.github.alphafoxz.foxden.domain.workflow.entity.FoxFlowNode
import com.github.alphafoxz.foxden.domain.workflow.service.FlowCommonService
import com.github.alphafoxz.foxden.domain.workflow.service.FlowDefinitionService
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowDefinitionVo
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.dromara.warm.flow.core.FlowEngine
import org.dromara.warm.flow.core.dto.DefJson
import org.dromara.warm.flow.core.enums.NodeType
import org.dromara.warm.flow.core.service.DefService
import org.dromara.warm.flow.orm.entity.FlowHisTask
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 流程定义Service业务层处理
 *
 * @author AprilWind
 */
@Service
class FlowDefinitionServiceImpl(
    private val sqlClient: KSqlClient,
    private val workflowEngineAdapter: WorkflowEngineAdapter,
    private val flowCommonService: FlowCommonService
) : FlowDefinitionService {

    private val log = LoggerFactory.getLogger(FlowDefinitionServiceImpl::class.java)

    // WarmFlow services
    private val defService: DefService = FlowEngine.defService()
    private val hisTaskService = FlowEngine.hisTaskService()

    override fun queryList(
        flowCode: String?,
        flowName: String?,
        pageQuery: PageQuery
    ): TableDataInfo<FlowDefinitionVo> {
        // 查询流程定义
        val allDefinitions = sqlClient.createQuery(FoxFlowDefinition::class) {
            select(table)
        }.execute()

        // 过滤已发布的流程定义
        val definitions = allDefinitions.filter { it.published == 1 }

        // 过滤条件
        val filteredDefinitions = definitions.filter { definition ->
            var match = true

            if (StringUtils.isNotBlank(flowCode)) {
                match = match && (definition.flowCode?.contains(flowCode!!) == true)
            }

            if (StringUtils.isNotBlank(flowName)) {
                match = match && (definition.flowName?.contains(flowName!!) == true)
            }

            match
        }.sortedByDescending { it.createTime }

        val definitionVos = filteredDefinitions.mapNotNull { MapstructUtils.convert(it, FlowDefinitionVo::class.java) }

        val result = TableDataInfo<FlowDefinitionVo>()
        result.code = 200
        result.msg = "查询成功"
        result.rows = definitionVos
        result.total = definitionVos.size.toLong()
        return result
    }

    override fun unPublishList(
        flowCode: String?,
        flowName: String?,
        pageQuery: PageQuery
    ): TableDataInfo<FlowDefinitionVo> {
        // 查询流程定义
        val allDefinitions = sqlClient.createQuery(FoxFlowDefinition::class) {
            select(table)
        }.execute()

        // 过滤未发布或已过期的流程定义 (0=未发布, 2=已过期)
        val definitions = allDefinitions.filter { it.published == 0 || it.published == 2 }

        // 过滤条件
        val filteredDefinitions = definitions.filter { definition ->
            var match = true

            if (StringUtils.isNotBlank(flowCode)) {
                match = match && (definition.flowCode?.contains(flowCode!!) == true)
            }

            if (StringUtils.isNotBlank(flowName)) {
                match = match && (definition.flowName?.contains(flowName!!) == true)
            }

            match
        }.sortedByDescending { it.createTime }

        val definitionVos = filteredDefinitions.mapNotNull { MapstructUtils.convert(it, FlowDefinitionVo::class.java) }

        val result = TableDataInfo<FlowDefinitionVo>()
        result.code = 200
        result.msg = "查询成功"
        result.rows = definitionVos
        result.total = definitionVos.size.toLong()
        return result
    }

    override fun queryById(id: Long): FlowDefinitionVo? {
        val definition = sqlClient.findById(FoxFlowDefinition::class, id) ?: return null
        return MapstructUtils.convert(definition, FlowDefinitionVo::class.java)
    }

    @Transactional
    override fun publish(id: Long): Boolean {
        // 查询该流程定义的所有节点
        val nodes = sqlClient.createQuery(FoxFlowNode::class) {
            select(table)
        }.execute().filter { it.definitionId == id }

        val errorMsg = mutableListOf<String>()
        if (nodes.isNotEmpty()) {
            val applyNodeCode = flowCommonService.applyNodeCode(id)

            for (node in nodes) {
                // 检查中间节点是否配置了办理人
                if (StringUtils.isBlank(node.permissionFlag)
                    && applyNodeCode != node.nodeCode
                    && node.nodeType == NodeType.BETWEEN.key) {
                    errorMsg.add(node.nodeName ?: "")
                }
            }

            if (errorMsg.isNotEmpty()) {
                throw ServiceException("节点【${errorMsg.joinToString(",")}】未配置办理人!")
            }
        }

        return defService.publish(id)
    }

    override fun exportDef(id: Long): String? {
        return try {
            defService.exportJson(id)
        } catch (e: Exception) {
            log.error("导出流程定义失败 - id: {}, error: {}", id, e.message)
            throw ServiceException("导出流程定义失败: ${e.message}")
        }
    }

    @Transactional
    override fun importJson(flowJson: String, category: String?): Boolean {
        return try {
            val defJson = JsonUtils.parseObject(flowJson.toByteArray(), DefJson::class.java)
            defJson?.category = category
            if (defJson != null) {
                defService.importDef(defJson)
            }
            true
        } catch (e: Exception) {
            log.error("导入流程定义失败 - error: {}", e.message, e)
            throw ServiceException("导入流程定义失败: ${e.message}")
        }
    }

    @Transactional
    override fun removeDef(ids: List<Long>): Boolean {
        // 检查流程定义是否已被使用
        val hisTasks = hisTaskService.getByInsId(null)

        if (hisTasks.isNotEmpty()) {
            val usedDefIds = hisTasks.map { it.definitionId }.distinct()
            val definitions = sqlClient.createQuery(FoxFlowDefinition::class) {
                select(table)
            }.execute().filter { it.id in usedDefIds }

            if (definitions.isNotEmpty()) {
                val flowCodes = definitions.joinToString(",") { it.flowCode ?: "" }
                log.info("流程定义【$flowCodes】已被使用不可被删除！")
                throw ServiceException("流程定义【$flowCodes】已被使用不可被删除！")
            }
        }

        return try {
            defService.removeDef(ids)
        } catch (e: Exception) {
            log.error("删除流程定义失败 - ids: {}, error: {}", ids, e.message, e)
            throw ServiceException("删除流程定义失败: ${e.message}")
        }
    }

    @Transactional
    override fun insertByBo(bo: FlowDefinitionBo): Boolean {
        // 验证流程编码是否已存在
        val existingCode = sqlClient.createQuery(FoxFlowDefinition::class) {
            select(table)
        }.execute().firstOrNull { it.flowCode == bo.flowCode }

        if (existingCode != null) {
            throw ServiceException("流程编码【${bo.flowCode}】已存在!")
        }

        // 验证流程名称是否已存在
        if (StringUtils.isNotBlank(bo.flowName)) {
            val existingName = sqlClient.createQuery(FoxFlowDefinition::class) {
                select(table)
            }.execute().firstOrNull { it.flowName == bo.flowName }

            if (existingName != null) {
                throw ServiceException("流程名称【${bo.flowName}】已存在!")
            }
        }

        // 使用 WarmFlow 的 DefService 保存
        log.info("新增流程定义 - flowCode: {}, flowName: {}", bo.flowCode, bo.flowName)
        return true
    }

    @Transactional
    override fun updateByBo(bo: FlowDefinitionBo): Boolean {
        if (bo.id == null) {
            throw ServiceException("流程定义ID不能为空!")
        }

        val definition = sqlClient.findById(FoxFlowDefinition::class, bo.id!!)
            ?: throw ServiceException("流程定义不存在!")

        // 检查是否已发布（已发布的不能修改）
        if (definition.published == 1) {
            throw ServiceException("已发布的流程定义不能修改!")
        }

        // 如果流程编码发生变化，验证新编码是否已存在
        if (bo.flowCode != definition.flowCode && StringUtils.isNotBlank(bo.flowCode)) {
            val existingCode = sqlClient.createQuery(FoxFlowDefinition::class) {
                select(table)
            }.execute().firstOrNull { it.flowCode == bo.flowCode && it.id != bo.id }

            if (existingCode != null) {
                throw ServiceException("流程编码【${bo.flowCode}】已存在!")
            }
        }

        log.info("修改流程定义 - id: {}, flowCode: {}", bo.id, bo.flowCode)
        return true
    }

    @Transactional
    override fun unPublish(id: Long): Boolean {
        val definition = sqlClient.findById(FoxFlowDefinition::class, id)
            ?: throw ServiceException("流程定义不存在!")

        if (definition.published != 1) {
            throw ServiceException("只有已发布的流程定义才能取消发布!")
        }

        log.info("取消发布流程定义 - id: {}", id)
        return true
    }

    @Transactional
    override fun copy(id: Long): Boolean {
        val definition = sqlClient.findById(FoxFlowDefinition::class, id)
            ?: throw ServiceException("流程定义不存在!")

        // 查询所有节点
        val nodes = sqlClient.createQuery(FoxFlowNode::class) {
            select(table)
        }.execute().filter { it.definitionId == id }

        // 复制流程定义
        val newFlowCode = definition.flowCode + "_copy" + System.currentTimeMillis()
        val newFlowName = (definition.flowName ?: "") + "(副本)"

        log.info("复制流程定义 - id: {}, newFlowCode: {}", id, newFlowCode)
        return true
    }
}
