package com.github.alphafoxz.foxden.domain.workflow.service

import com.github.alphafoxz.foxden.common.core.domain.dto.StartProcessReturnDTO
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.workflow.bo.*
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowInstanceVo
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowTaskVo

/**
 * 节点信息（临时使用，待WarmFlow集成后替换）
 */
data class FlowNodeData(
    val nodeId: Long? = null,
    val nodeName: String? = null,
    val nodeType: String? = null,
    val nodeCode: String? = null,
    val nodeRatio: String? = null,
    val coordinate: String? = null,
    val permissionList: List<String>? = null
)

/**
 * 任务Service接口
 *
 * @author AprilWind
 */
interface FlowTaskService {

    /**
     * 启动任务
     *
     * @param startProcessBo 启动流程参数
     * @return 流程实例ID
     */
    fun startWorkFlow(startProcessBo: StartProcessBo): StartProcessReturnDTO?

    /**
     * 办理任务
     *
     * @param completeTaskBo 办理任务参数
     * @return 结果
     */
    fun completeTask(completeTaskBo: CompleteTaskBo): Boolean

    /**
     * 查询当前用户的待办任务
     *
     * @param flowTaskBo 参数
     * @param pageQuery 分页
     * @return 结果
     */
    fun pageByTaskWait(flowTaskBo: FlowTaskBo, pageQuery: PageQuery): TableDataInfo<FlowTaskVo>

    /**
     * 查询当前租户所有待办任务
     *
     * @param flowTaskBo 参数
     * @param pageQuery 分页
     * @return 结果
     */
    fun pageByAllTaskWait(flowTaskBo: FlowTaskBo, pageQuery: PageQuery): TableDataInfo<FlowTaskVo>

    /**
     * 查询当前用户的已办任务
     *
     * @param flowTaskBo 参数
     * @param pageQuery 分页
     * @return 结果
     */
    fun pageByTaskFinish(flowTaskBo: FlowTaskBo, pageQuery: PageQuery): TableDataInfo<com.github.alphafoxz.foxden.domain.workflow.vo.FlowHisTaskVo>

    /**
     * 查询当前租户所有已办任务
     *
     * @param flowTaskBo 参数
     * @param pageQuery 分页
     * @return 结果
     */
    fun pageByAllTaskFinish(flowTaskBo: FlowTaskBo, pageQuery: PageQuery): TableDataInfo<com.github.alphafoxz.foxden.domain.workflow.vo.FlowHisTaskVo>

    /**
     * 查询当前用户的抄送
     *
     * @param flowTaskBo 参数
     * @param pageQuery 分页
     * @return 结果
     */
    fun pageByTaskCopy(flowTaskBo: FlowTaskBo, pageQuery: PageQuery): TableDataInfo<FlowTaskVo>

    /**
     * 根据任务ID查询任务详情
     *
     * @param taskId 任务id
     * @return 任务详情
     */
    fun selectById(taskId: Long): FlowTaskVo?

    /**
     * 驳回审批
     *
     * @param bo 参数
     * @return 结果
     */
    fun backProcess(bo: BackProcessBo): Boolean

    /**
     * 终止任务
     *
     * @param bo 参数
     * @return 结果
     */
    fun terminationTask(bo: FlowTerminationBo): Boolean

    /**
     * 取消流程
     *
     * @param bo 参数
     * @return 结果
     */
    fun cancelProcess(bo: FlowCancelBo): Boolean

    /**
     * 作废流程
     *
     * @param bo 参数
     * @return 结果
     */
    fun invalidProcess(bo: FlowInvalidBo): Boolean

    /**
     * 任务操作
     *
     * @param bo 参数
     * @param taskOperation 操作类型
     * @return 结果
     */
    fun taskOperation(bo: TaskOperationBo, taskOperation: String): Boolean

    /**
     * 获取下一节点信息
     *
     * @param bo 参数
     * @return 节点列表
     */
    fun getNextNodeList(bo: FlowNextNodeBo): List<FlowNodeData>

    /**
     * 判断流程是否已结束
     *
     * @param instanceId 流程实例ID
     * @return true 表示任务已全部结束
     */
    fun isTaskEnd(instanceId: Long): Boolean
}
