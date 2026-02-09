package com.github.alphafoxz.foxden.domain.workflow.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 流程任务表 flow_task
 */
@Entity
@Table(name = "flow_task")
interface FlowTask : CommDelFlag, CommId {
    /**
     * 流程定义ID
     */
    val definitionId: Long

    /**
     * 流程实例ID
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
     * 节点类型
     */
    val nodeType: Int

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
