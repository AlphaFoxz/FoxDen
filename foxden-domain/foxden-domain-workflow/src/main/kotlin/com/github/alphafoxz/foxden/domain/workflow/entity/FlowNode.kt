package com.github.alphafoxz.foxden.domain.workflow.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 流程节点表 flow_node
 */
@Entity
@Table(name = "flow_node")
interface FlowNode : CommDelFlag, CommId {
    /**
     * 节点类型
     */
    val nodeType: Int

    /**
     * 流程定义ID
     */
    val definitionId: Long

    /**
     * 节点编码
     */
    val nodeCode: String

    /**
     * 节点名称
     */
    val nodeName: String?

    /**
     * 权限标识
     */
    val permissionFlag: String?

    /**
     * 节点比例
     */
    val nodeRatio: String?

    /**
     * 坐标
     */
    val coordinate: String?

    /**
     * 任意节点跳转
     */
    val anyNodeSkip: String?

    /**
     * 监听器类型
     */
    val listenerType: String?

    /**
     * 监听器路径
     */
    val listenerPath: String?

    /**
     * 表单是否自定义（0否 1是）
     */
    val formCustom: String?

    /**
     * 表单路径
     */
    val formPath: String?

    /**
     * 流程版本
     */
    val version: String

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
