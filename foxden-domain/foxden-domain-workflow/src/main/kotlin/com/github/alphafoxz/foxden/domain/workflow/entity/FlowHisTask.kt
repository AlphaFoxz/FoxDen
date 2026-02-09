package com.github.alphafoxz.foxden.domain.workflow.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 流程历史任务表 flow_his_task
 */
@Entity
@Table(name = "flow_his_task")
interface FlowHisTask : CommDelFlag, CommId {
    /**
     * 流程定义ID
     */
    val definitionId: Long

    /**
     * 流程实例ID
     */
    val instanceId: Long

    /**
     * 任务ID
     */
    val taskId: Long

    /**
     * 节点编码
     */
    val nodeCode: String?

    /**
     * 节点名称
     */
    val nodeName: String?

    /**
     * 节点类型
     */
    val nodeType: Int?

    /**
     * 目标节点编码
     */
    val targetNodeCode: String?

    /**
     * 目标节点名称
     */
    val targetNodeName: String?

    /**
     * 审批人
     */
    val approver: String?

    /**
     * 协作类型（0审批 1转办 2抄送 3委派）
     */
    val cooperateType: Int

    /**
     * 协作人
     */
    val collaborator: String?

    /**
     * 跳过类型
     */
    val skipType: String?

    /**
     * 流程状态
     */
    val flowStatus: String

    /**
     * 表单是否自定义（0否 1是）
     */
    val formCustom: String?

    /**
     * 表单路径
     */
    val formPath: String?

    /**
     * 扩展字段
     */
    val ext: String?

    /**
     * 消息
     */
    val message: String?

    /**
     * 流程变量
     */
    val variable: String?

    /**
     * 创建时间
     */
    val createTime: java.time.LocalDateTime?

    /**
     * 更新时间
     */
    val updateTime: java.time.LocalDateTime?

    /**
     * 租户ID
     */
    val tenantId: String?
}
