package com.github.alphafoxz.foxden.domain.workflow.bo

import java.io.Serializable

/**
 * 流程实例请求对象
 *
 * @author AprilWind
 */
data class FlowInstanceBo(
    /**
     * 流程定义名称
     */
    var flowName: String? = null,

    /**
     * 流程定义编码
     */
    var flowCode: String? = null,

    /**
     * 任务发起人
     */
    var startUserId: String? = null,

    /**
     * 业务id
     */
    var businessId: String? = null,

    /**
     * 流程分类id
     */
    var category: String? = null,

    /**
     * 任务名称
     */
    var nodeName: String? = null,

    /**
     * 申请人Ids
     */
    var createByIds: List<String>? = null
) : Serializable
