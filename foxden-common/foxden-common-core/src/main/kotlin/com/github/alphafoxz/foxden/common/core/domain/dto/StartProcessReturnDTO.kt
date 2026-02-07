package com.github.alphafoxz.foxden.common.core.domain.dto

import java.io.Serializable

/**
 * 启动流程返回对象
 */
data class StartProcessReturnDTO(
    /**
     * 流程实例id
     */
    var processInstanceId: Long? = null,

    /**
     * 任务id
     */
    var taskId: Long? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
