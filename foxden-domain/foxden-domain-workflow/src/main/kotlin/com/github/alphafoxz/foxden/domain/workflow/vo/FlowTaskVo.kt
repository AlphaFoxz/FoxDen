package com.github.alphafoxz.foxden.domain.workflow.vo

import java.io.Serializable
import java.util.*

/**
 * 任务视图对象
 *
 * @author AprilWind
 */
data class FlowTaskVo(
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
     * 流程实例表id
     */
    var instanceId: Long? = null,

    /**
     * 流程定义名称
     */
    var flowName: String? = null,

    /**
     * 业务id
     */
    var businessId: String? = null,

    /**
     * 节点编码
     */
    var nodeCode: String? = null,

    /**
     * 节点名称
     */
    var nodeName: String? = null,

    /**
     * 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    var nodeType: Int? = null,

    /**
     * 权限标识
     */
    var permissionList: List<String>? = null,

    /**
     * 审批表单是否自定义（Y是 N否）
     */
    var formCustom: String? = null,

    /**
     * 审批表单
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
     * 流程状态
     */
    var flowStatus: String? = null,

    /**
     * 流程分类id
     */
    var category: String? = null,

    /**
     * 流程分类名称
     */
    var categoryName: String? = null,

    /**
     * 流程状态名称
     */
    var flowStatusName: String? = null,

    /**
     * 办理人类型
     */
    var type: String? = null,

    /**
     * 办理人ids
     */
    var assigneeIds: String? = null,

    /**
     * 办理人名称
     */
    var assigneeNames: String? = null,

    /**
     * 抄送人id
     */
    var processedBy: String? = null,

    /**
     * 抄送人名称
     */
    var processedByName: String? = null,

    /**
     * 流程签署比例值
     */
    var nodeRatio: String? = null,

    /**
     * 申请人id
     */
    var createBy: String? = null,

    /**
     * 申请人名称
     */
    var createByName: String? = null,

    /**
     * 是否为申请人节点
     */
    var applyNode: Boolean? = null,

    /**
     * 按钮权限
     */
    var buttonList: List<ButtonPermissionVo>? = null,

    /**
     * 抄送对象列表
     */
    var copyList: List<FlowCopyVo>? = null,

    /**
     * 自定义参数
     */
    var varList: Map<String, String>? = null,

    /**
     * 业务编码
     */
    var businessCode: String? = null,

    /**
     * 业务标题
     */
    var businessTitle: String? = null
) : Serializable
