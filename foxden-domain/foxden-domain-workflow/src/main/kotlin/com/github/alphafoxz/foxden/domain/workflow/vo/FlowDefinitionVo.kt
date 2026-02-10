package com.github.alphafoxz.foxden.domain.workflow.vo

import java.io.Serializable
import java.util.*

/**
 * 流程定义视图对象
 *
 * @author AprilWind
 */
data class FlowDefinitionVo(
    var id: Long? = null,

    /**
     * 创建时间
     */
    var createTime: Date? = null,

    /**
     * 更新时间
     */
    var updateTime: Date? = null,

    /**
     * 租户ID
     */
    var tenantId: String? = null,

    /**
     * 删除标记
     */
    var delFlag: String? = null,

    /**
     * 流程定义编码
     */
    var flowCode: String? = null,

    /**
     * 流程定义名称
     */
    var flowName: String? = null,

    /**
     * 流程分类id
     */
    var category: String? = null,

    /**
     * 流程分类名称
     */
    var categoryName: String? = null,

    /**
     * 流程版本
     */
    var version: String? = null,

    /**
     * 是否发布（0未发布 1已发布 9失效）
     */
    var isPublish: Int? = null,

    /**
     * 审批表单是否自定义（Y是 N否）
     */
    var formCustom: String? = null,

    /**
     * 审批表单路径
     */
    var formPath: String? = null,

    /**
     * 流程激活状态（0挂起 1激活）
     */
    var activityStatus: Int? = null,

    /**
     * 监听器类型
     */
    var listenerType: String? = null,

    /**
     * 监听器路径
     */
    var listenerPath: String? = null,

    /**
     * 扩展字段
     */
    var ext: String? = null
) : Serializable
