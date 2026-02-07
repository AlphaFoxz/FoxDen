package com.github.alphafoxz.foxden.common.core.domain.dto

import java.io.Serializable

/**
 * 抄送
 */
data class FlowCopyDTO(
    /**
     * 用户id
     */
    var userId: Long? = null,

    /**
     * 用户名称
     */
    var userName: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
