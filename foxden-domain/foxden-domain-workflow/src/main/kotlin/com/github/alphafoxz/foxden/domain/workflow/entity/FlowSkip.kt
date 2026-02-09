package com.github.alphafoxz.foxden.domain.workflow.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 流程跳转表 flow_skip
 */
@Entity
@Table(name = "flow_skip")
interface FlowSkip : CommDelFlag, CommId {
    /**
     * 流程定义ID
     */
    val definitionId: Long

    /**
     * 当前节点编码
     */
    val nowNodeCode: String

    /**
     * 当前节点类型
     */
    val nowNodeType: Int?

    /**
     * 下一个节点编码
     */
    val nextNodeCode: String

    /**
     * 下一个节点类型
     */
    val nextNodeType: Int?

    /**
     * 跳转名称
     */
    val skipName: String?

    /**
     * 跳转类型
     */
    val skipType: String?

    /**
     * 跳转条件
     */
    val skipCondition: String?

    /**
     * 坐标
     */
    val coordinate: String?

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
