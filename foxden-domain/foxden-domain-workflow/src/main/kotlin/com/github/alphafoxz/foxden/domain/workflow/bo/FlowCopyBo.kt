package com.github.alphafoxz.foxden.domain.workflow.bo

import java.io.Serializable

/**
 * 抄送业务对象
 *
 * @author AprilWind
 */
data class FlowCopyBo(
    /**
     * 用户id
     */
    var userId: Long? = null,

    /**
     * 用户名称
     */
    var userName: String? = null
) : Serializable
