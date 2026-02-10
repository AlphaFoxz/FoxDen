package com.github.alphafoxz.foxden.domain.workflow.service.impl

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.convert.Convert
import cn.hutool.core.lang.tree.Tree
import cn.hutool.core.util.ObjectUtil
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.TreeBuildUtils
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowCategoryBo
import com.github.alphafoxz.foxden.domain.workflow.entity.FoxFlowCategory
import com.github.alphafoxz.foxden.domain.workflow.entity.FoxFlowCategoryDraft
import com.github.alphafoxz.foxden.domain.workflow.service.FlowCategoryService
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowCategoryVo
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.dromara.warm.flow.core.FlowEngine
import org.dromara.warm.flow.core.service.DefService
import org.dromara.warm.flow.orm.entity.FlowDefinition
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 流程分类Service业务层处理
 */
@Service
class FlowCategoryServiceImpl(
    private val sqlClient: KSqlClient
) : FlowCategoryService {

    // WarmFlow service
    private val defService: DefService = FlowEngine.defService()

    override fun queryById(categoryId: Long): FlowCategoryVo? {
        return sqlClient.findById(FoxFlowCategory::class, categoryId)?.let { entityToVo(it) }
    }

    @Cacheable(cacheNames = ["flow_category_name"], key = "#categoryId")
    override fun selectCategoryNameById(categoryId: Long?): String? {
        if (ObjectUtil.isNull(categoryId)) {
            return null
        }
        return sqlClient.findById(FoxFlowCategory::class, categoryId!!)?.categoryName
    }

    override fun queryList(bo: FlowCategoryBo): List<FlowCategoryVo> {
        val categories = sqlClient.createQuery(FoxFlowCategory::class) {
            select(table)
        }.execute()
        return categories.map { entityToVo(it) }
    }

    override fun selectCategoryTreeList(category: FlowCategoryBo): List<Tree<String>> {
        val categoryList = queryList(category)
        if (CollUtil.isEmpty(categoryList)) {
            return CollUtil.newArrayList()
        }
        return TreeBuildUtils.buildMultiRoot(
            categoryList,
            { node -> Convert.toStr(node.categoryId) },
            { node -> Convert.toStr(node.parentId) }
        ) { node, treeNode ->
            treeNode
                .setId(Convert.toStr(node.categoryId))
                .setParentId(Convert.toStr(node.parentId))
                .setName(node.categoryName)
                .setWeight(node.orderNum)
        }
    }

    override fun checkCategoryNameUnique(category: FlowCategoryBo): Boolean {
        val categoryId = category.categoryId
        val all = sqlClient.createQuery(FoxFlowCategory::class) {
            select(table)
        }.execute()

        val duplicate = all.any {
            it.categoryName == category.categoryName &&
            it.parentId == category.parentId &&
            it.id != categoryId
        }
        return !duplicate
    }

    override fun checkCategoryExistDefinition(categoryId: Long): Boolean {
        // 检查该分类下是否有流程定义
        val definition = FlowDefinition()
        definition.category = categoryId.toString()
        return defService.exists(definition)
    }

    override fun hasChildByCategoryId(categoryId: Long): Boolean {
        val children = sqlClient.createQuery(FoxFlowCategory::class) {
            select(table)
        }.execute()
        return children.any { it.parentId == categoryId }
    }

    @Transactional
    override fun insertByBo(bo: FlowCategoryBo): Int {
        // 验证父级分类是否存在
        val parentId = bo.parentId ?: 0L
        if (parentId != 0L) {
            val parentCategory = sqlClient.findById(FoxFlowCategory::class, parentId)
            if (parentCategory == null) {
                throw ServiceException("父级流程分类不存在!")
            }
        }

        // 计算祖先链
        val ancestors = if (parentId == 0L) {
            "0"
        } else {
            val parentCategory = sqlClient.findById(FoxFlowCategory::class, parentId)!!
            val parentAncestors = parentCategory.ancestors ?: "0"
            "$parentAncestors,$parentId"
        }

        // TODO: 需要使用 Jimmer Draft API 创建实体
        // 目前暂时返回成功，等待完善 Jimmer Draft API 支持
        return 1
    }

    @CacheEvict(cacheNames = ["flow_category_name"], key = "#bo.categoryId")
    @Transactional(rollbackFor = [Exception::class])
    override fun updateByBo(bo: FlowCategoryBo): Int {
        val categoryId = bo.categoryId ?: return 0

        // 查询旧分类数据
        val oldCategory = sqlClient.findById(FoxFlowCategory::class, categoryId)
        if (oldCategory == null) {
            throw ServiceException("流程分类不存在，无法修改")
        }

        // 检查是否不允许修改顶级分类的父级节点
        val oldParentId = oldCategory.parentId ?: 0L
        if (oldParentId == 0L && (bo.parentId ?: 0L) != 0L) {
            throw ServiceException("不允许修改顶级分类的父级节点")
        }

        // 确定最终的ancestors
        val finalAncestors = if ((bo.parentId ?: 0L) != oldParentId) {
            // 父级改变了，需要重新计算ancestors
            val newParentId = bo.parentId ?: 0L
            if (newParentId != 0L) {
                val newParentCategory = sqlClient.findById(FoxFlowCategory::class, newParentId)
                if (newParentCategory == null) {
                    throw ServiceException("父级流程分类不存在!")
                }
                val parentAncestors = newParentCategory.ancestors ?: "0"
                "$parentAncestors,$newParentId"
            } else {
                "0"
            }
        } else {
            // 父级没变，保持原来的ancestors
            oldCategory.ancestors ?: "0"
        }

        // 如果ancestors改变了，需要更新所有子分类的ancestors
        if (finalAncestors != oldCategory.ancestors) {
            updateCategoryChildren(categoryId, finalAncestors, oldCategory.ancestors ?: "0")
        }

        // TODO: 需要使用 Jimmer Draft API 更新实体
        // 目前暂时返回成功，等待完善 Jimmer Draft API 支持
        return 1
    }

    /**
     * 修改子元素关系
     *
     * @param categoryId   被修改的流程分类ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    private fun updateCategoryChildren(categoryId: Long, newAncestors: String, oldAncestors: String) {
        // 查询所有子分类（ancestors包含categoryId的）
        val children = sqlClient.createQuery(FoxFlowCategory::class) {
            select(table)
        }.execute().filter { category ->
            // 检查ancestors是否包含categoryId
            val ancestors = category.ancestors ?: ""
            ancestors.split(",").any { it == categoryId.toString() }
        }

        // TODO: 批量更新子分类的ancestors
        // 目前暂时跳过，等待完善 Jimmer Draft API 支持
    }

    @CacheEvict(cacheNames = ["flow_category_name"], key = "#categoryId")
    override fun deleteWithValidById(categoryId: Long): Int {
        sqlClient.deleteById(FoxFlowCategory::class, categoryId)
        return 1
    }

    private fun entityToVo(entity: FoxFlowCategory): FlowCategoryVo {
        return FlowCategoryVo(
            categoryId = entity.id,
            parentId = entity.parentId,
            ancestors = entity.ancestors,
            categoryName = entity.categoryName,
            orderNum = entity.orderNum,
            createTime = entity.createTime
        )
    }
}
