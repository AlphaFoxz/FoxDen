package com.github.alphafoxz.foxden.domain.workflow.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 流程实例表 flow_instance
 */
@Entity
@Table(name = "flow_instance")
interface FoxFlowInstance : CommDelFlag, CommId {
    /**
     * 流程定义ID（对应 flow_definition 表的 id）
     */
    val definitionId: Long

    /**
     * 业务编号
     */
    val businessId: String

    /**
     * 节点类型（1开始 2结束 3用户 4抄送 5条件）
     */
    val nodeType: Int

    /**
     * 节点编码
     */
    val nodeCode: String

    /**
     * 节点名称
     */
    val nodeName: String?

    /**
     * 流程变量
     */
    val variable: String?

    /**
     * 流程状态
     */
    val flowStatus: String

    /**
     * 活动状态（1编辑中 2已发布 3已停用）
     */
    val activityStatus: Int

    /**
     * 流程定义JSON
     */
    val defJson: String?

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
     * 扩展字段
     */
    val ext: String?

    /**
     * 租户ID
     */
    val tenantId: String?
}
