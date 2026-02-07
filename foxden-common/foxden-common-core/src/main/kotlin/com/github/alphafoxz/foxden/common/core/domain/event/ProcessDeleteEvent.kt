package com.github.alphafoxz.foxden.common.core.domain.event

import java.io.Serializable

/**
 * 删除流程监听
 */
data class ProcessDeleteEvent(
    /**
     * 租户ID
     */
    var tenantId: String? = null,

    /**
     * 流程定义编码
     */
    var flowCode: String? = null,

    /**
     * 业务id
     */
    var businessId: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
