package com.github.alphafoxz.foxden.domain.workflow.vo

import java.io.Serializable
import java.util.*

/**
 * 历史任务视图对象
 *
 * @author AprilWind
 */
data class FlowHisTaskVo(
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
     * 流程实例表id
     */
    var instanceId: Long? = null,

    /**
     * 任务表id
     */
    var taskId: Long? = null,

    /**
     * 协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签)
     */
    var cooperateType: Int? = null,

    /**
     * 协作方式名称
     */
    var cooperateTypeName: String? = null,

    /**
     * 业务id
     */
    var businessId: String? = null,

    /**
     * 开始节点编码
     */
    var nodeCode: String? = null,

    /**
     * 开始节点名称
     */
    var nodeName: String? = null,

    /**
     * 开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    var nodeType: Int? = null,

    /**
     * 目标节点编码
     */
    var targetNodeCode: String? = null,

    /**
     * 结束节点名称
     */
    var targetNodeName: String? = null,

    /**
     * 审批者
     */
    var approver: String? = null,

    /**
     * 审批者名称
     */
    var approveName: String? = null,

    /**
     * 协作人
     */
    var collaborator: String? = null,

    /**
     * 权限标识
     */
    var permissionList: List<String>? = null,

    /**
     * 跳转类型（PASS通过 REJECT退回 NONE无动作）
     */
    var skipType: String? = null,

    /**
     * 流程状态
     */
    var flowStatus: String? = null,

    /**
     * 任务状态
     */
    var flowTaskStatus: String? = null,

    /**
     * 流程状态名称
     */
    var flowStatusName: String? = null,

    /**
     * 审批意见
     */
    var message: String? = null,

    /**
     * 业务详情
     */
    var ext: String? = null,

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
     * 审批表单是否自定义（Y是 N否）
     */
    var formCustom: String? = null,

    /**
     * 审批表单路径
     */
    var formPath: String? = null,

    /**
     * 流程定义编码
     */
    var flowCode: String? = null,

    /**
     * 流程版本号
     */
    var version: String? = null,

    /**
     * 运行时长
     */
    var runDuration: String? = null,

    /**
     * 业务编码
     */
    var businessCode: String? = null,

    /**
     * 业务标题
     */
    var businessTitle: String? = null
) : Serializable
