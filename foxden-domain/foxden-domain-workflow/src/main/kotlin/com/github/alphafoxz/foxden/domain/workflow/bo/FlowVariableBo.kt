package com.github.alphafoxz.foxden.domain.workflow.bo

import java.io.Serializable

/**
 * 流程变量业务对象
 *
 * @author AprilWind
 */
data class FlowVariableBo(
    /**
     * 流程实例id
     */
    var instanceId: Long? = null,

    /**
     * 变量map
     */
    var variables: Map<String, Any>? = null
) : Serializable
