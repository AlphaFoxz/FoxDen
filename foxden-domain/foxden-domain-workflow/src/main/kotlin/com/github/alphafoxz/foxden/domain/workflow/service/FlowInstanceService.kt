package com.github.alphafoxz.foxden.domain.workflow.service

import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowCancelBo
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowInstanceBo
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowVariableBo
import com.github.alphafoxz.foxden.domain.workflow.entity.FoxFlowInstance
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowInstanceVo

/**
 * 流程实例Service接口
 *
 * @author AprilWind
 */
interface FlowInstanceService {

    /**
     * 分页查询正在运行的流程实例
     *
     * @param flowInstanceBo 流程实例
     * @param pageQuery 分页
     * @return 流程实例分页列表
     */
    fun selectRunningInstanceList(flowInstanceBo: FlowInstanceBo, pageQuery: PageQuery): TableDataInfo<FlowInstanceVo>

    /**
     * 分页查询已结束的流程实例
     *
     * @param flowInstanceBo 流程实例
     * @param pageQuery 分页
     * @return 流程实例分页列表
     */
    fun selectFinishInstanceList(flowInstanceBo: FlowInstanceBo, pageQuery: PageQuery): TableDataInfo<FlowInstanceVo>

    /**
     * 根据业务id查询流程实例详细信息
     *
     * @param businessId 业务id
     * @return 流程实例详情
     */
    fun queryByBusinessId(businessId: String): FlowInstanceVo?

    /**
     * 查询流程实例详情
     *
     * @param instanceId 流程实例ID
     * @return 流程实例详情
     */
    fun queryDetailById(instanceId: Long): FlowInstanceVo?

    /**
     * 获取当前登录人发起的流程实例
     *
     * @param flowInstanceBo 流程实例
     * @param pageQuery 分页
     * @return 流程实例分页列表
     */
    fun selectCurrentInstanceList(flowInstanceBo: FlowInstanceBo, pageQuery: PageQuery): TableDataInfo<FlowInstanceVo>

    /**
     * 获取流程图和流程记录
     *
     * @param businessId 业务id
     * @return 包含流程实例ID和流转记录的Map
     */
    fun flowHisTaskList(businessId: String): Map<String, Any>

    /**
     * 获取流程变量
     *
     * @param instanceId 实例id
     * @return 包含变量列表和原始JSON字符串的Map
     */
    fun instanceVariable(instanceId: Long): Map<String, Any>

    /**
     * 设置流程变量
     *
     * @param bo 参数
     * @return 结果
     */
    fun updateVariable(bo: FlowVariableBo): Boolean

    /**
     * 删除流程实例
     *
     * @param instanceIds 流程实例ID列表
     * @return 结果
     */
    fun deleteByIds(instanceIds: List<Long>): Boolean

    /**
     * 按照实例id删除已完成的流程实例
     *
     * @param instanceIds 实例id
     * @return 结果
     */
    fun deleteHisByInstanceIds(instanceIds: List<Long>): Boolean

    /**
     * 根据业务id查询流程实例
     *
     * @param businessId 业务id
     * @return 流程实例
     */
    fun selectInstByBusinessId(businessId: String): FoxFlowInstance?

    /**
     * 按照实例id查询流程实例
     *
     * @param instanceId 实例id
     * @return 流程实例
     */
    fun selectInstById(instanceId: Long): FoxFlowInstance?

    /**
     * 按任务id查询实例
     *
     * @param taskId 任务id
     * @return 流程实例
     */
    fun selectByTaskId(taskId: Long): FoxFlowInstance?

    /**
     * 按照实例id更新状态
     *
     * @param instanceId 实例id
     * @param status 状态
     */
    fun updateStatus(instanceId: Long, status: String)

    /**
     * 按照业务id删除流程实例
     *
     * @param businessIds 业务id列表
     * @return 结果
     */
    fun deleteByBusinessIds(businessIds: List<String>): Boolean

    /**
     * 撤销流程申请
     *
     * @param bo 参数
     * @return 结果
     */
    fun cancelProcessApply(bo: FlowCancelBo): Boolean

    /**
     * 激活流程实例
     *
     * @param instanceId 实例id
     * @return 结果
     */
    fun active(instanceId: Long): Boolean

    /**
     * 停用流程实例
     *
     * @param instanceId 实例id
     * @return 结果
     */
    fun unActive(instanceId: Long): Boolean
}
