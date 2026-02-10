package com.github.alphafoxz.foxden.domain.workflow.service.impl

import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.domain.dto.TaskAssigneeDTO
import com.github.alphafoxz.foxden.common.core.domain.model.TaskAssigneeBody
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowSpelBo
import com.github.alphafoxz.foxden.domain.workflow.entity.FlowSpel
import com.github.alphafoxz.foxden.domain.workflow.service.FlowSpelService
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowSpelVo
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 流程spel表达式定义Service业务层处理
 *
 * @author AprilWind
 */
@Service
class FlowSpelServiceImpl(
    private val sqlClient: KSqlClient
) : FlowSpelService {

    override fun queryById(id: Long?): FlowSpelVo? {
        if (id == null) return null
        val entity = sqlClient.findById(FlowSpel::class, id)
        return entity?.let { entityToVo(it) }
    }

    override fun queryPageList(bo: FlowSpelBo, pageQuery: PageQuery): TableDataInfo<FlowSpelVo> {
        // 分页查询流程 SpEL 表达式
        // 1. 构建基础查询条件
        // 2. 添加过滤条件：
        //    - componentName: 模糊查询
        //    - methodName: 模糊查询
        //    - viewSpel: 模糊查询
        //    - status: 精确匹配（默认查询正常状态的）
        // 3. 按创建时间倒序排序
        // 4. 分页返回结果
        val list = queryList(bo)
        val total = list.size.toLong()

        // 手动分页（后续可改用 Jimmer 原生分页）
        val pageNum = pageQuery.pageNum ?: 1
        val pageSize = pageQuery.pageSize ?: 10
        val start = (pageNum - 1) * pageSize
        val end = (start + pageSize).coerceAtMost(list.size)
        val paged = if (start < list.size) {
            list.subList(start, end)
        } else {
            emptyList()
        }

        val result = TableDataInfo<FlowSpelVo>()
        result.code = 200
        result.msg = "查询成功"
        result.rows = paged
        result.total = total
        return result
    }

    override fun queryList(bo: FlowSpelBo): List<FlowSpelVo> {
        // 查询所有 SpEL 表达式定义
        val all = sqlClient.createQuery(FlowSpel::class) {
            select(table)
        }.execute()

        // 应用过滤条件
        val filtered = all.filter { spel ->
            var match = true

            // componentName 模糊匹配
            bo.componentName?.let { compName ->
                match = match && (spel.componentName?.contains(compName) == true)
            }

            // methodName 模糊匹配
            bo.methodName?.let { methodName ->
                match = match && (spel.methodName?.contains(methodName) == true)
            }

            // viewSpel 模糊匹配
            bo.viewSpel?.let { viewSpel ->
                match = match && (spel.viewSpel?.contains(viewSpel) == true)
            }

            // status 精确匹配
            bo.status?.let { status ->
                match = match && (spel.status == status)
            }

            match
        }

        return filtered.map { entityToVo(it) }
    }

    @Transactional
    override fun insertByBo(bo: FlowSpelBo): Boolean {
        val newEntity = FlowSpel {
            componentName = bo.componentName ?: ""
            methodName = bo.methodName ?: ""
            methodParams = bo.methodParams
            viewSpel = bo.viewSpel ?: ""
            status = bo.status ?: SystemConstants.NORMAL
            remark = bo.remark
        }
        sqlClient.save(newEntity)
        return newEntity.id != null
    }

    @Transactional
    override fun updateByBo(bo: FlowSpelBo): Boolean {
        val id = bo.id ?: return false
        val existing = sqlClient.findById(FlowSpel::class, id) ?: return false
        val updated = FlowSpel(existing) {
            if (bo.componentName != null) componentName = bo.componentName
            if (bo.methodName != null) methodName = bo.methodName
            if (bo.methodParams != null) methodParams = bo.methodParams
            if (bo.viewSpel != null) viewSpel = bo.viewSpel
            if (bo.status != null) status = bo.status
            if (bo.remark != null) remark = bo.remark
        }
        sqlClient.save(updated)
        return true
    }

    @Transactional
    override fun deleteWithValidByIds(ids: Collection<Long>, isValid: Boolean): Boolean {
        if (isValid) {
            // 可以在这里添加业务校验
        }
        ids.forEach { sqlClient.deleteById(FlowSpel::class, it) }
        return true
    }

    override fun selectSpelByTaskAssigneeList(taskQuery: TaskAssigneeBody): TaskAssigneeDTO {
        val pageQuery = PageQuery().apply {
            pageNum = taskQuery.pageNum
            pageSize = taskQuery.pageSize
        }
        val bo = FlowSpelBo(
            viewSpel = taskQuery.handlerCode,
            remark = taskQuery.handlerName,
            status = SystemConstants.NORMAL
        )
        val page = queryPageList(bo, pageQuery)

        // 转换为 TaskAssigneeDTO 格式
        val handlers = TaskAssigneeDTO.convertToHandlerList(
            page.rows ?: emptyList(),
            { vo -> vo.viewSpel ?: "" },          // storageId
            { vo -> "" },                           // handlerCode (空字符串)
            { vo -> vo.remark ?: "" },              // handlerName
            { vo -> "" },                           // groupName (空字符串)
            { vo -> java.util.Date.from(vo.createTime?.atZone(java.time.ZoneId.systemDefault())?.toInstant() ?: java.time.Instant.now()) }
        )

        return TaskAssigneeDTO(page.total ?: 0L, handlers)
    }

    override fun selectRemarksBySpels(viewSpels: List<String>): Map<String, String> {
        if (viewSpels.isEmpty()) {
            return emptyMap()
        }
        val entities = sqlClient.createQuery(FlowSpel::class) {
            select(table)
        }.execute().filter { it.viewSpel != null && it.viewSpel in viewSpels }

        return entities.associate { entity ->
            (entity.viewSpel ?: "") to (entity.remark ?: "")
        }
    }

    private fun entityToVo(entity: FlowSpel): FlowSpelVo {
        return FlowSpelVo(
            id = entity.id,
            componentName = entity.componentName,
            methodName = entity.methodName,
            methodParams = entity.methodParams,
            viewSpel = entity.viewSpel,
            status = entity.status,
            remark = entity.remark,
            createTime = entity.createTime
        )
    }
}
