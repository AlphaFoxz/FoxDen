package com.github.alphafoxz.foxden.domain.workflow.bo

import java.io.Serializable

/**
 * 终止流程业务对象
 *
 * @author AprilWind
 */
data class FlowTerminationBo(
    /**
     * 任务id
     */
    var taskId: Long? = null,

    /**
     * 终止原因/意见
     */
    var message: String? = null,

    /**
     * 终止意见（别名）
     */
    var comment: String? = null
) : Serializable
