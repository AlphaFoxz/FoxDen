package com.github.alphafoxz.foxden.domain.workflow.bo

import java.io.Serializable

/**
 * 催办任务业务对象
 *
 * @author AprilWind
 */
data class FlowUrgeTaskBo(
    /**
     * 任务id
     */
    var taskId: Long? = null,

    /**
     * 催办消息
     */
    var message: String? = null
) : Serializable
