package com.github.alphafoxz.foxden.domain.workflow.bo

import java.io.Serializable

/**
 * 下一个节点业务对象
 *
 * @author AprilWind
 */
data class FlowNextNodeBo(
    /**
     * 流程实例id
     */
    var instanceId: Long? = null,

    /**
     * 任务id
     */
    var taskId: Long? = null,

    /**
     * 流程变量
     */
    var variables: Map<String, Any>? = null
) : Serializable
