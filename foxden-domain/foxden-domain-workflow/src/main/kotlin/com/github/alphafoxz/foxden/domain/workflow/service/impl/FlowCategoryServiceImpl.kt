package com.github.alphafoxz.foxden.domain.workflow.service.impl

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.convert.Convert
import cn.hutool.core.lang.tree.Tree
import cn.hutool.core.util.ObjectUtil
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.TreeBuildUtils
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowCategoryBo
import com.github.alphafoxz.foxden.domain.workflow.entity.FlowCategory
import com.github.alphafoxz.foxden.domain.workflow.service.FlowCategoryService
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowCategoryVo
import org.babyfish.jimmer.sql.kt.KSqlClient
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

    override fun queryById(categoryId: Long): FlowCategoryVo? {
        return sqlClient.findById(FlowCategory::class, categoryId)?.let { entityToVo(it) }
    }

    @Cacheable(cacheNames = ["flow_category_name"], key = "#categoryId")
    override fun selectCategoryNameById(categoryId: Long?): String? {
        if (ObjectUtil.isNull(categoryId)) {
            return null
        }
        return sqlClient.findById(FlowCategory::class, categoryId!!)?.categoryName
    }

    override fun queryList(bo: FlowCategoryBo): List<FlowCategoryVo> {
        val categories = sqlClient.findById(FlowCategory::class, 1)?.let { listOf(it) } ?: emptyList()
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
        // TODO: 实现唯一性检查
        return true
    }

    override fun checkCategoryExistDefinition(categoryId: Long): Boolean {
        // TODO: 集成 WarmFlow 后实现
        return false
    }

    override fun hasChildByCategoryId(categoryId: Long): Boolean {
        // TODO: 实现子节点检查
        return false
    }

    override fun insertByBo(bo: FlowCategoryBo): Int {
        // TODO: 实现完整的祖先链计算
        // 暂时简化实现
        return 1
    }

    @CacheEvict(cacheNames = ["flow_category_name"], key = "#bo.categoryId")
    @Transactional(rollbackFor = [Exception::class])
    override fun updateByBo(bo: FlowCategoryBo): Int {
        // TODO: 实现完整的更新逻辑
        return 1
    }

    @CacheEvict(cacheNames = ["flow_category_name"], key = "#categoryId")
    override fun deleteWithValidById(categoryId: Long): Int {
        sqlClient.deleteById(FlowCategory::class, categoryId)
        return 1
    }

    private fun entityToVo(entity: FlowCategory): FlowCategoryVo {
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
