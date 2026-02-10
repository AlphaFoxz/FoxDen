package com.github.alphafoxz.foxden.domain.workflow.bo

import jakarta.validation.constraints.NotBlank
import java.io.Serializable

/**
 * 取消流程业务对象
 *
 * @author AprilWind
 */
data class FlowCancelBo(
    /**
     * 业务id
     */
    var businessId: String? = null,

    /**
     * 流程实例id（与 businessId 二选一）
     */
    var instanceId: Long? = null,

    /**
     * 取消原因
     */
    var message: String? = null
) : Serializable
