package com.github.alphafoxz.foxden.domain.workflow.service

import com.github.alphafoxz.foxden.common.core.domain.dto.TaskAssigneeDTO
import com.github.alphafoxz.foxden.common.core.domain.model.TaskAssigneeBody
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowSpelBo
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowSpelVo

/**
 * 流程spel表达式定义Service接口
 *
 * @author AprilWind
 */
interface FlowSpelService {

    /**
     * 查询流程spel表达式定义
     *
     * @param id 主键
     * @return 流程spel表达式定义
     */
    fun queryById(id: Long?): FlowSpelVo?

    /**
     * 分页查询流程spel表达式定义列表
     *
     * @param bo 查询条件
     * @param pageQuery 分页参数
     * @return 流程spel表达式定义分页列表
     */
    fun queryPageList(bo: FlowSpelBo, pageQuery: PageQuery): TableDataInfo<FlowSpelVo>

    /**
     * 查询符合条件的流程spel表达式定义列表
     *
     * @param bo 查询条件
     * @return 流程spel表达式定义列表
     */
    fun queryList(bo: FlowSpelBo): List<FlowSpelVo>

    /**
     * 新增流程spel表达式定义
     *
     * @param bo 流程spel表达式定义
     * @return 是否新增成功
     */
    fun insertByBo(bo: FlowSpelBo): Boolean

    /**
     * 修改流程spel表达式定义
     *
     * @param bo 流程spel表达式定义
     * @return 是否修改成功
     */
    fun updateByBo(bo: FlowSpelBo): Boolean

    /**
     * 校验并批量删除流程spel表达式定义信息
     *
     * @param ids 待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    fun deleteWithValidByIds(ids: Collection<Long>, isValid: Boolean): Boolean

    /**
     * 查询spel并返回任务指派的列表，支持分页
     *
     * @param taskQuery 查询条件
     * @return 办理人
     */
    fun selectSpelByTaskAssigneeList(taskQuery: TaskAssigneeBody): TaskAssigneeDTO?

    /**
     * 根据视图 SpEL 表达式列表，查询对应的备注信息
     *
     * @param viewSpels SpEL 表达式列表
     * @return 映射表：key 为 SpEL 表达式，value 为对应备注；若为空则返回空 Map
     */
    fun selectRemarksBySpels(viewSpels: List<String>): Map<String, String>
}
