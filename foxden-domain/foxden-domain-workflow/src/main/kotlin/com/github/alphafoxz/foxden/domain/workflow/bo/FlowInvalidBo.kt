package com.github.alphafoxz.foxden.domain.workflow.bo

import java.io.Serializable

/**
 * 作废流程业务对象
 *
 * @author AprilWind
 */
data class FlowInvalidBo(
    /**
     * 流程实例id
     */
    var instanceId: Long? = null,

    /**
     * 作废原因
     */
    var message: String? = null
) : Serializable
