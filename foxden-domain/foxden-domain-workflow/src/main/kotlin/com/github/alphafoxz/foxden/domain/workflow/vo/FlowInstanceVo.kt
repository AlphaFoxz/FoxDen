package com.github.alphafoxz.foxden.domain.workflow.vo

import java.io.Serializable
import java.util.*

/**
 * 流程实例视图对象
 *
 * @author AprilWind
 */
data class FlowInstanceVo(
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
     * 对应flow_definition表的id
     */
    var definitionId: Long? = null,

    /**
     * 流程定义名称
     */
    var flowName: String? = null,

    /**
     * 流程定义编码
     */
    var flowCode: String? = null,

    /**
     * 业务id
     */
    var businessId: String? = null,

    /**
     * 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    var nodeType: Int? = null,

    /**
     * 流程节点编码
     */
    var nodeCode: String? = null,

    /**
     * 流程节点名称
     */
    var nodeName: String? = null,

    /**
     * 流程变量
     */
    var variable: String? = null,

    /**
     * 流程状态
     */
    var flowStatus: String? = null,

    /**
     * 流程状态名称
     */
    var flowStatusName: String? = null,

    /**
     * 流程激活状态（0挂起 1激活）
     */
    var activityStatus: Int? = null,

    /**
     * 审批表单是否自定义（Y是 N否）
     */
    var formCustom: String? = null,

    /**
     * 审批表单路径
     */
    var formPath: String? = null,

    /**
     * 扩展字段
     */
    var ext: String? = null,

    /**
     * 流程定义版本
     */
    var version: String? = null,

    /**
     * 创建者
     */
    var createBy: String? = null,

    /**
     * 申请人
     */
    var createByName: String? = null,

    /**
     * 流程分类id
     */
    var category: String? = null,

    /**
     * 流程分类名称
     */
    var categoryName: String? = null,

    /**
     * 业务编码
     */
    var businessCode: String? = null,

    /**
     * 业务标题
     */
    var businessTitle: String? = null
) : Serializable
