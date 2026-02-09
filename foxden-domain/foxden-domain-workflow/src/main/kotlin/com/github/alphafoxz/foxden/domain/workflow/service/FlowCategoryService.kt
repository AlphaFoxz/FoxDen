package com.github.alphafoxz.foxden.domain.workflow.service

import cn.hutool.core.lang.tree.Tree
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowCategoryBo
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowCategoryVo

/**
 * 流程分类Service接口
 */
interface FlowCategoryService {
    /**
     * 查询流程分类
     *
     * @param categoryId 主键
     * @return 流程分类
     */
    fun queryById(categoryId: Long): FlowCategoryVo?

    /**
     * 根据流程分类ID查询流程分类名称
     *
     * @param categoryId 流程分类ID
     * @return 流程分类名称
     */
    fun selectCategoryNameById(categoryId: Long?): String?

    /**
     * 查询符合条件的流程分类列表
     *
     * @param bo 查询条件
     * @return 流程分类列表
     */
    fun queryList(bo: FlowCategoryBo): List<FlowCategoryVo>

    /**
     * 查询流程分类树结构信息
     *
     * @param category 流程分类信息
     * @return 流程分类树信息集合
     */
    fun selectCategoryTreeList(category: FlowCategoryBo): List<Tree<String>>

    /**
     * 校验流程分类名称是否唯一
     *
     * @param category 流程分类信息
     * @return 结果
     */
    fun checkCategoryNameUnique(category: FlowCategoryBo): Boolean

    /**
     * 查询流程分类是否存在流程定义
     *
     * @param categoryId 流程分类ID
     * @return 结果 true 存在 false 不存在
     */
    fun checkCategoryExistDefinition(categoryId: Long): Boolean

    /**
     * 是否存在流程分类子节点
     *
     * @param categoryId 流程分类ID
     * @return 结果
     */
    fun hasChildByCategoryId(categoryId: Long): Boolean

    /**
     * 新增流程分类
     *
     * @param bo 流程分类
     * @return 是否新增成功
     */
    fun insertByBo(bo: FlowCategoryBo): Int

    /**
     * 修改流程分类
     *
     * @param bo 流程分类
     * @return 是否修改成功
     */
    fun updateByBo(bo: FlowCategoryBo): Int

    /**
     * 删除流程分类信息
     *
     * @param categoryId 主键
     * @return 是否删除成功
     */
    fun deleteWithValidById(categoryId: Long): Int
}
