package com.github.alphafoxz.foxden.common.core.domain.event

import java.io.Serializable

/**
 * 总体流程监听
 */
data class ProcessEvent(
    /**
     * 租户ID
     */
    var tenantId: String? = null,

    /**
     * 流程定义编码
     */
    var flowCode: String? = null,

    /**
     * 实例id
     */
    var instanceId: Long? = null,

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
     * 流程状态
     */
    var status: String? = null,

    /**
     * 办理参数
     */
    var params: Map<String, Any>? = null,

    /**
     * 当为true时为申请人节点办理
     */
    var submit: Boolean? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
