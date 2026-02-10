package com.github.alphafoxz.foxden.domain.workflow.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 流程任务表 flow_task
 *
 * 存储工作流中的待办任务信息
 * - 每个任务关联一个流程定义（definitionId）
 * - 每个任务关联一个流程实例（instanceId）
 * - 任务状态由 flowStatus 字段控制
 * - 节点类型（nodeType）：0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关
 */
@Entity
@Table(name = "flow_task")
interface FoxFlowTask : CommDelFlag, CommId {
    /**
     * 流程定义ID（多对一关联）
     * 对应 flow_definition 表的 id
     */
    val definitionId: Long

    /**
     * 流程实例ID（多对一关联）
     * 对应 flow_instance 表的 id
     */
    val instanceId: Long

    /**
     * 节点编码
     */
    val nodeCode: String

    /**
     * 节点名称
     */
    val nodeName: String?

    /**
     * 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    val nodeType: Int

    /**
     * 流程状态
     * 0: 待提交 1: 审批中 2: 审批通过 4: 终止 5: 作废 6: 撤销 8: 已完成 9: 已退回 10: 失效 11: 拿回
     */
    val flowStatus: String

    /**
     * 表单是否自定义（Y是 N否）
     */
    val formCustom: String?

    /**
     * 表单路径
     */
    val formPath: String?

    /**
     * 创建时间
     */
    val createTime: java.time.LocalDateTime?

    /**
     * 创建者
     */
    val createBy: String?

    /**
     * 更新时间
     */
    val updateTime: java.time.LocalDateTime?

    /**
     * 更新者
     */
    val updateBy: String?

    /**
     * 租户ID
     */
    val tenantId: String?
}
