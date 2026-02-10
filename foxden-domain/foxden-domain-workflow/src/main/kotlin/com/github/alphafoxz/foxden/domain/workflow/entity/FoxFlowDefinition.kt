package com.github.alphafoxz.foxden.domain.workflow.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 流程定义表 flow_definition
 *
 * 存储工作流的基本定义信息，包括流程的版本、状态、表单配置等
 */
@Entity
@Table(name = "flow_definition")
interface FoxFlowDefinition : CommDelFlag, CommId {
    /**
     * 流程编码（唯一标识）
     */
    val flowCode: String

    /**
     * 流程名称
     */
    val flowName: String

    /**
     * 流程模型（CLASSICS-经典模型）
     */
    val modelValue: String?

    /**
     * 流程分类（对应 flow_category 表的 category_id）
     */
    val category: String?

    /**
     * 流程版本
     */
    val version: String

    /**
     * 是否发布（0未发布 1已发布）
     */
    @org.babyfish.jimmer.sql.Column(name = "is_publish")
    val published: Int

    /**
     * 表单是否自定义（0否 1是）
     */
    val formCustom: String?

    /**
     * 表单路径
     */
    val formPath: String?

    /**
     * 活动状态（1编辑中 2已发布 3已停用）
     */
    val activityStatus: Int

    /**
     * 监听器类型
     */
    val listenerType: String?

    /**
     * 监听器路径
     */
    val listenerPath: String?

    /**
     * 扩展字段
     */
    val ext: String?

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
